package earlyeffect.hub

import earlyeffect.docs.EarlyEffectTheme
import specular.ExampleRunner
import specular.site.*
import zio.*
import zio.http.Client

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths, StandardCopyOption}

/** Builds the Early Effect org hub from published micro-site `metadata.json` URLs.
  *
  * URL allowlist: `catalog-urls.txt` at the repo root (one https URL per line).
  * The landing page SSRs optional first-paint cards, then the Ascent client
  * (`LiveCatalog.bootstrap`) re-fetches allowlisted manifests on each visit so version
  * bumps show up without rebuilding the hub. Rebuild the hub when the allowlist changes.
  *
  * Branding comes from `early-effect-docs-theme`. Extra rasters under `images/` (e.g. PNG logo,
  * favicon) are still copied into the site output.
  */
object BuildHub extends ZIOAppDefault:

  private val FallbackSpecular = ProjectMeta(
    name = "specular",
    organization = "rocks.earlyeffect",
    version = "0.4.0",
    scalaVersion = "3.8.4",
    title = Some("Specular"),
    description = Some("Code-first tests-as-docs site generator for Scala."),
    language = Some("Scala"),
    homepage = Some("https://github.com/early-effect/specular"),
    docsUrl = Some("https://www.earlyeffect.rocks/specular/"),
  )

  def run =
    val out  = Paths.get("target/site")
    val urls = readCatalogUrls
    (for
      fallback <- loadFallbackProjects(urls)
      catalog = ProjectCatalog.live(urls, fallback =
        if fallback.nonEmpty then fallback else Vector(FallbackSpecular)
      )
      model = SiteModel(
        title = "Early Effect",
        basePath = "/",
        description = Some("Open-source functional Scala and ZIO libraries."),
        logo = Some(EarlyEffectTheme.logoHref),
        brand = Some(
          Brand(
            name = "Early Effect",
            tagline = Some("Open-source functional Scala & ZIO libraries."),
            links = Vector(
              BrandLink("GitHub", "https://github.com/early-effect"),
              BrandLink("Maven Central", "https://central.sonatype.com/namespace/rocks.earlyeffect"),
            ),
          )
        ),
        home = Some(
          HomePage(
            hero = Some(
              Hero(
                title = "Early Effect",
                subtitle = Some("Open-source functional Scala & ZIO libraries."),
                links = Vector(
                  BrandLink("GitHub", "https://github.com/early-effect"),
                  BrandLink("Maven Central", "https://central.sonatype.com/namespace/rocks.earlyeffect"),
                ),
                image = Some(EarlyEffectTheme.logoHref),
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
            title = Some("Early Effect"),
            description = Some("Open-source functional Scala and ZIO libraries."),
            homepage = Some("https://www.earlyeffect.rocks"),
            docsUrl = Some("https://www.earlyeffect.rocks/"),
          )
        ),
        clientScript = Some("assets/client.js"),
      )
      result <- ZIO.serviceWithZIO[SiteBuilder](_.buildSite(model, out))
      _      <- copyStaticAssets(out)
      _      <- copyClientBundle(out)
      _      <- EarlyEffectTheme.writeLogo(out)
      _      <- injectFavicon(out)
      _      <- Console.printLine(s"Wrote hub → $out (${result.pages.size} files)")
    yield ()).provide(
      Client.default,
      MarkdownRenderer.live,
      ExampleRunner.live,
      HtmlSsr.live,
      SiteWriter.live,
      NavBuilder.live,
      EarlyEffectTheme.live,
      PageTemplate.live,
      LandingTemplate.live,
      SiteBuilder.live,
    )
  end run

  private def copyStaticAssets(out: Path): Task[Unit] =
    ZIO.attempt {
      val srcDir = Paths.get("images")
      if Files.isDirectory(srcDir) then
        val destDir = out.resolve("images")
        Files.createDirectories(destDir)
        Files.list(srcDir).forEach { src =>
          if Files.isRegularFile(src) then
            Files.copy(src, destDir.resolve(src.getFileName), StandardCopyOption.REPLACE_EXISTING)
        }
      val favicon = Paths.get("images/favicon.ico")
      if Files.isRegularFile(favicon) then
        Files.copy(favicon, out.resolve("favicon.ico"), StandardCopyOption.REPLACE_EXISTING)
      ()
    }

  private def copyClientBundle(out: Path): Task[Unit] =
    ZIO.attempt {
      val marker = Paths.get("target/hub-client-js.path")
      if !Files.isRegularFile(marker) then
        throw new IllegalStateException(
          "JS client not linked; run sbt specularSite (or hubJS/fastLinkJS) first."
        )
      val src  = Paths.get(Files.readString(marker, StandardCharsets.UTF_8).nn.trim)
      val dest = out.resolve("assets/client.js")
      Files.createDirectories(dest.getParent)
      Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING)
      ()
    }

  private def injectFavicon(out: Path): Task[Unit] =
    ZIO.attempt {
      val index = out.resolve("index.html")
      if Files.isRegularFile(index) then
        var html = Files.readString(index, StandardCharsets.UTF_8).nn
        if !html.contains("""rel="icon"""") then
          html = html.replaceFirst(
            "</title>",
            """</title><link rel="icon" href="favicon.ico">""",
          )
          Files.writeString(index, html, StandardCharsets.UTF_8)
      ()
    }

  private def loadFallbackProjects(urls: Vector[String]): RIO[Client, Vector[ProjectMeta]] =
    ZIO
      .foreach(urls) { url =>
        ProjectMetaHttp
          .fetchOne(url)
          .tapError(e => Console.printLine(s"WARN: skip $url (${e.getMessage})").orDie)
          .option
      }
      .map(_.flatten)
      .tap { projects =>
        Console.printLine(
          if projects.isEmpty then "Catalog fallback: (none; using static Specular card)"
          else s"Catalog SSR fallback: ${projects.map(_.name).mkString(", ")}"
        )
      }

  private def readCatalogUrls: Vector[String] =
    val path = Paths.get("catalog-urls.txt")
    if !Files.isRegularFile(path) then Vector.empty
    else
      Files
        .readString(path, StandardCharsets.UTF_8)
        .nn
        .linesIterator
        .map(_.trim)
        .filter(l => l.nonEmpty && !l.startsWith("#"))
        .toVector
end BuildHub
