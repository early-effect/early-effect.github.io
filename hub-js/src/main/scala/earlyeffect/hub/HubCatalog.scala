package earlyeffect.hub

import ascent.Dom
import specular.site.{LiveCatalogIds, ProjectMeta}
import zio.*

import scala.scalajs.js
import ascent.dom as d

/** Fetch allowlisted `metadata.json` manifests for the SPA catalog. */
object HubCatalog:

  def allowlist: UIO[Vector[String]] =
    ZIO.succeed:
      val nodes = Dom.document.querySelectorAll(s"""link[rel="${LiveCatalogIds.MetaLinkRel}"]""")
      (0 until nodes.length).toVector
        .flatMap { i =>
          val node = nodes.item(i)
          if node == null then None
          else
            val href = node.asInstanceOf[d.Element].getAttribute("href")
            Option(href).map(_.nn.trim).filter(_.nonEmpty)
        }
        .filter(ProjectMeta.isAllowedMetaUrl)

  def fetch(urls: Vector[String]): UIO[Vector[ProjectMeta]] =
    ZIO
      .foreach(urls) { url =>
        fetchOne(url)
          .tapError(e => Console.printLine(s"WARN: skip ${e.render}").orElseSucceed(()))
          .option
      }
      .map(_.flatten)

  private def fetchOne(url: String): IO[HubError, ProjectMeta] =
    for
      _ <-
        if ProjectMeta.isAllowedMetaUrl(url) then ZIO.unit
        else ZIO.fail(HubError.RefusedUrl(url))
      response <- ZIO
        .fromPromiseJS(d.window.fetch(url).asInstanceOf[js.Promise[d.Response]])
        .mapError(cause => HubError.Transport(url, detail(cause)))
      _ <-
        if response.ok then ZIO.unit
        else ZIO.fail(HubError.HttpStatus(url, response.status))
      body <- ZIO
        .fromPromiseJS(response.text().asInstanceOf[js.Promise[String]])
        .mapError(cause => HubError.Transport(url, detail(cause)))
      _ <-
        if body.length <= ProjectMeta.MaxBodyBytes then ZIO.unit
        else ZIO.fail(HubError.BodyTooLarge(url, body.length))
      meta <- ZIO.fromEither(ProjectMeta.parseJson(body)).mapError(reason => HubError.InvalidMeta(url, reason))
    yield meta.withSanitizedLinks

  private def detail(cause: Throwable): String =
    Option(cause.getMessage).map(_.nn).filter(_.nonEmpty).getOrElse(cause.toString)
end HubCatalog
