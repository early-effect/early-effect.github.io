package earlyeffect.hub

import specular.ExampleRunner
import specular.site.*
import zio.*
import zio.http.Client

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths, StandardCopyOption}

/** Builds the Early Effect org hub from published micro-site `metadata.json` URLs.
  *
  * URL allowlist: `catalog-urls.txt` at the repo root (one https URL per line).
  * If a URL fails to fetch (e.g. docs not deployed yet), a fallback Specular card is used
  * so the hub can still build before the first Pages deploy.
  *
  * Static assets under `images/` (logo, favicon) are copied into the site output. Until the
  * published `specular-site` release includes `Hero.image`, the logo is injected into the
  * landing HTML after the build.
  */
object BuildHub extends ZIOAppDefault:

  private val HeroImageSrc = "images/logo.png"

  private val FallbackSpecular = ProjectMeta(
    name = "specular",
    organization = "rocks.earlyeffect",
    version = "0.1.0",
    scalaVersion = "3.8.4",
    title = Some("Specular"),
    description = Some("Code-first tests-as-docs site generator for Scala."),
    language = Some("Scala"),
    homepage = Some("https://github.com/early-effect/specular"),
    docsUrl = Some("https://early-effect.github.io/specular/"),
  )

  def run =
    val out  = Paths.get("target/site")
    val urls = readCatalogUrls
    (for
      catalog <- loadCatalog(urls)
      model = SiteModel(
        title = "Early Effect",
        basePath = "/",
        description = Some("Open-source functional Scala and ZIO libraries."),
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
        clientScript = None,
      )
      result <- ZIO.serviceWithZIO[SiteBuilder](_.buildSite(model, out))
      _      <- copyStaticAssets(out)
      _      <- injectHeroLogo(out)
      _      <- Console.printLine(s"Wrote hub → $out (${result.pages.size} files)")
    yield ()).provide(
      Client.default,
      MarkdownRenderer.live,
      ExampleRunner.live,
      HtmlSsr.live,
      SiteWriter.live,
      NavBuilder.live,
      Theme.earlyEffect,
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

  /** Inject hero logo + favicon for sites built against specular-site without Hero.image yet. */
  private def injectHeroLogo(out: Path): Task[Unit] =
    ZIO.attempt {
      val index = out.resolve("index.html")
      if Files.isRegularFile(index) then
        var html = Files.readString(index, StandardCharsets.UTF_8).nn
        if !html.contains("specular-hero-image") then
          val img =
            s"""<img class="specular-hero-image" src="$HeroImageSrc" alt="Early Effect" width="160" height="160">"""
          html = html.replaceFirst(
            """(<h1[^>]*class="[^"]*specular-hero-title[^"]*"[^>]*>)""",
            img + "$1",
          )
        if !html.contains("""rel="icon"""") then
          html = html.replaceFirst(
            "</title>",
            """</title><link rel="icon" href="favicon.ico">""",
          )
        Files.writeString(index, html, StandardCharsets.UTF_8)

      val css = out.resolve("assets/theme.css")
      if Files.isRegularFile(css) then
        val extra =
          """
            |.specular-hero-image{display:block;width:10rem;height:10rem;margin:0 auto 1.25rem;object-fit:contain;border-radius:1.25rem;}
            |""".stripMargin
        val body = Files.readString(css, StandardCharsets.UTF_8).nn
        if !body.contains(".specular-hero-image") then
          Files.writeString(css, body + extra, StandardCharsets.UTF_8)
      ()
    }

  private def loadCatalog(urls: Vector[String]): RIO[Client, ProjectCatalog] =
    ZIO
      .foreach(urls) { url =>
        ProjectMeta
          .fetchOne(url)
          .tapError(e => Console.printLine(s"WARN: skip $url (${e.getMessage})").orDie)
          .option
      }
      .map(_.flatten)
      .map { projects =>
        if projects.nonEmpty then ProjectCatalog(projects)
        else ProjectCatalog(Vector(FallbackSpecular))
      }
      .tap { c =>
        Console.printLine(s"Catalog: ${c.projects.map(_.name).mkString(", ")}")
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
