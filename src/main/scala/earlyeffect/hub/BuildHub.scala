package earlyeffect.hub

import specular.ExampleRunner
import specular.site.*
import zio.*
import zio.http.Client

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/** Builds the Early Effect org hub from published micro-site `metadata.json` URLs.
  *
  * URL allowlist: `catalog-urls.txt` at the repo root (one https URL per line).
  * If a URL fails to fetch (e.g. docs not deployed yet), a fallback Specular card is used
  * so the hub can still build before the first Pages deploy.
  */
object BuildHub extends ZIOAppDefault:

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
        else
          ProjectCatalog(Vector(FallbackSpecular))
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
