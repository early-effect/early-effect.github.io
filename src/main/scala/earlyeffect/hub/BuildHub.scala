package earlyeffect.hub

import earlyeffect.docs.EarlyEffectTheme
import specular.ExampleRunner
import specular.site.*
import zio.*
import zio.http.Client

import java.nio.file.{Files, Path, Paths}

/** Builds the Early Effect org hub from published micro-site `metadata.json` URLs.
  *
  * URL allowlist: `catalog-urls.txt` at the repo root (one https URL per line). The landing SSRs the designed page
  * (`HubView`). JS only refreshes catalog versions and the proof flipper. Rebuild the hub when the allowlist or chrome
  * changes.
  *
  * Branding comes from `early-effect-docs-theme` (`writeLogo`). Extra rasters under `images/` are copied into output.
  */
object BuildHub extends ZIOAppDefault:

  private val FallbackSpecular = ProjectMeta(
    name = "specular",
    organization = "rocks.earlyeffect",
    version = "0.10.1",
    scalaVersion = "3.8.4",
    title = Some("Specular"),
    description = Some("Code-first tests-as-docs site generator for Scala."),
    language = Some("Scala"),
    homepage = Some("https://github.com/early-effect/specular"),
    docsUrl = Some("https://www.earlyeffect.rocks/specular/"),
  )

  def run =
    val out = Paths.get("target/site")
    (for
      urls     <- readCatalogUrls
      fallback <- loadFallbackProjects(urls)
      catalog = ProjectCatalog.live(
        urls,
        fallback =
          if fallback.nonEmpty then fallback else Vector(FallbackSpecular),
      )
      model = SiteModel(
        title = HubCopy.title,
        basePath = "/",
        description = Some(HubCopy.description),
        logo = Some(EarlyEffectTheme.logoHref),
        brand = Some(
          Brand(
            name = HubCopy.title,
            tagline = Some(HubCopy.tagline),
            links = Vector(
              BrandLink("GitHub", HubCopy.githubOrg),
              BrandLink("Maven Central", HubCopy.mavenCentral),
              BrandLink("X", HubCopy.xProfile),
            ),
          )
        ),
        home = Some(
          HomePage(
            hero = Some(
              Hero(
                title = HubCopy.title,
                subtitle = Some(HubCopy.tagline),
                links = Vector(
                  BrandLink("GitHub", HubCopy.githubOrg),
                  BrandLink("Maven Central", HubCopy.mavenCentral),
                ),
                image = Some(EarlyEffectTheme.heroImageHref),
              )
            ),
            sections = Vector(catalog),
          )
        ),
        meta = Some(
          ProjectMeta(
            name = "early-effect",
            organization = "rocks.earlyeffect",
            version = "1.0.0",
            scalaVersion = "3.8.4",
            title = Some(HubCopy.title),
            description = Some(HubCopy.description),
            homepage = Some(HubCopy.origin),
            docsUrl = Some(s"${HubCopy.origin}/"),
          )
        ),
        clientScript = Some("assets/client.js"),
      )
      result <- lift("build site")(ZIO.serviceWithZIO[SiteBuilder](_.buildSite(model, out)))
      _      <- copyStaticAssets(out)
      _      <- copyClientBundle(out)
      _      <- lift("write logo")(EarlyEffectTheme.writeLogo(out))
      _      <- injectFavicon(out)
      _      <- assertLanding(out)
      _      <- Console.printLine(s"Wrote hub → $out (${result.pages.size} files)").orElseSucceed(())
    yield ()).provideLayer(Client.default ++ hubLayers)
  end run

  /** Chalkboard theme plus a custom [[HubLanding]] instead of Specular's stock catalog landing. */
  private val hubLayers =
    ZLayer.make[SiteBuilder](
      Theme.fromTokens(EarlyEffectTheme.tokens),
      MarkdownRenderer.live,
      ExampleRunner.live,
      HtmlSsr.live,
      SiteWriter.live,
      NavBuilder.live,
      PageTemplate.live,
      HubLanding.live,
      SiteBuilder.live,
    )

  private def copyStaticAssets(out: Path): IO[HubError, Unit] =
    val srcDir = Paths.get("images")
    if !Files.isDirectory(srcDir) then ZIO.unit
    else
      val destDir = out.resolve("images")
      for
        _     <- HubIo.createDirectories(destDir)
        files <- HubIo.listRegularFiles(srcDir)
        _     <- ZIO.foreachDiscard(files)(src => HubIo.copy(src, destDir.resolve(src.getFileName)))
        favicon = Paths.get("images/favicon.ico")
        _ <- HubIo.copy(favicon, out.resolve("favicon.ico")).when(Files.isRegularFile(favicon)).unit
      yield ()
    end if
  end copyStaticAssets

  private def copyClientBundle(out: Path): IO[HubError, Unit] =
    val marker = Paths.get("target/hub-client-js.path")
    if !Files.isRegularFile(marker) then ZIO.fail(HubError.ClientNotLinked)
    else
      for
        raw <- HubIo.readUtf8(marker)
        src  = Paths.get(raw.trim)
        dest = out.resolve("assets/client.js")
        _ <- HubIo.createDirectories(dest.getParent)
        _ <- HubIo.copy(src, dest)
        _ <- bustClientCache(out, dest)
      yield ()
    end if
  end copyClientBundle

  /** Pages/Fastly cache `client.js` for 10 minutes. A content hash on the script URL makes a new bundle a new cache key
    * so a stale island cannot sit in front of fresh HTML.
    */
  private def bustClientCache(out: Path, js: Path): IO[HubError, Unit] =
    for
      bytes <- HubIo.readBytes(js)
      hash  = Integer.toUnsignedString(java.util.Arrays.hashCode(bytes), 36)
      index = out.resolve("index.html")
      html <- HubIo.readUtf8(index)
      needle = """src="assets/client.js""""
      next   = html.replace(needle, s"""src="assets/client.js?v=$hash"""")
      _ <-
        if next == html then ZIO.fail(HubError.LandingIncomplete(Vector(needle)))
        else HubIo.writeUtf8(index, next)
    yield ()
  end bustClientCache

  private def injectFavicon(out: Path): IO[HubError, Unit] =
    val index = out.resolve("index.html")
    if !Files.isRegularFile(index) then ZIO.unit
    else
      HubIo.readUtf8(index).flatMap { html =>
        if html.contains("""rel="icon"""") then ZIO.unit
        else
          HubIo.writeUtf8(
            index,
            html.replaceFirst("</title>", """</title><link rel="icon" href="favicon.ico">"""),
          )
      }
    end if
  end injectFavicon

  private def loadFallbackProjects(urls: Vector[String]): ZIO[Client, HubError, Vector[ProjectMeta]] =
    ZIO
      .foreach(urls) { url =>
        lift(s"fetch $url")(ProjectMetaHttp.fetchOne(url))
          .tapError(e => Console.printLine(s"WARN: skip ${e.render}").orElseSucceed(()))
          .option
      }
      .map(_.flatten)
      .tap { projects =>
        val line =
          if projects.isEmpty then "Catalog fallback: (none; using static Specular card)"
          else s"Catalog SSR fallback: ${projects.map(_.name).mkString(", ")}"
        Console.printLine(line).orElseSucceed(())
      }

  private def assertLanding(out: Path): IO[HubError, Unit] =
    HubIo.readUtf8(out.resolve("index.html")).flatMap { html =>
      val need = Vector(
        HubCopy.tagline,
        HubCopy.makerName,
        "Write",
        "Prove",
        "Ship",
        HubCopy.manifesto,
        "Act I",
        s"""id="${HubView.ProofMountId}"""",
        s"""id="${HubView.catalogMountId("write")}"""",
      )
      val missing = need.filterNot(html.contains)
      if missing.isEmpty then ZIO.unit
      else ZIO.fail(HubError.LandingIncomplete(missing))
    }

  private def readCatalogUrls: IO[HubError, Vector[String]] =
    val path = Paths.get("catalog-urls.txt")
    if !Files.isRegularFile(path) then ZIO.succeed(Vector.empty)
    else
      HubIo.readUtf8(path).map { raw =>
        raw.linesIterator.map(_.trim).filter(l => l.nonEmpty && !l.startsWith("#")).toVector
      }

  private def lift[R, A](op: String)(zio: ZIO[R, Throwable, A]): ZIO[R, HubError, A] =
    zio.mapError(e => HubError.Build(op, Option(e.getMessage).map(_.nn).getOrElse(e.toString)))
end BuildHub
