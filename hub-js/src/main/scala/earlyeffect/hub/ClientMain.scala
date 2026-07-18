package earlyeffect.hub

import ascent.*
import ascent.ast.UI
import ascent.dom
import specular.site.*
import zio.*

import scala.scalajs.js

/** Browser entry: refresh hub catalog cards from allowlisted metadata.json URLs.
  *
  * Specular 0.5.0's `LiveCatalog.bootstrap` remounts a nested `.specular-catalog-grid`,
  * which collapses the layout to one column. Remount card children into the existing
  * `#specular-live-catalog` grid instead. Drop this once Specular ≥ 0.5.1 ships
  * (https://github.com/early-effect/specular/pull/15) and call `LiveCatalog.bootstrap`.
  */
object ClientMain extends ZIOAppDefault:

  def run =
    for
      _ <- remountCatalogCards
      _ <- ZIO.never
    yield ()

  private def remountCatalogCards: UIO[Unit] =
    val root = Dom.document.getElementById(LiveCatalogIds.MountId)
    if root == null then ZIO.unit
    else
      val cardClass = Option(root.getAttribute("data-card-class")).filter(_.nn.nonEmpty).getOrElse("")
      for
        urls     <- readAllowlist
        projects <- fetchProjects(urls)
        _        <- ZIO.succeed { root.innerHTML = "" }
        _        <- AscentApp.mount(UI.Fragment(CatalogCards.cards(projects, cardClass)), root)
      yield ()

  private def readAllowlist: UIO[Vector[String]] =
    ZIO.succeed:
      val nodes = Dom.document.querySelectorAll(s"""link[rel="${LiveCatalogIds.MetaLinkRel}"]""")
      (0 until nodes.length).toVector.flatMap { i =>
        val node = nodes.item(i)
        if node == null then None
        else
          Option(node.asInstanceOf[dom.Element].getAttribute("href"))
            .map(_.nn.trim)
            .filter(_.nonEmpty)
      }.filter(ProjectMeta.isAllowedMetaUrl)

  private def fetchProjects(urls: Vector[String]): UIO[Vector[ProjectMeta]] =
    ZIO.foreach(urls)(fetchOne(_).option).map(_.flatten)

  private def fetchOne(url: String): Task[ProjectMeta] =
    for
      _ <- ZIO
        .fail(new IllegalArgumentException(s"Refusing non-http(s) metadata URL: $url"))
        .unless(ProjectMeta.isAllowedMetaUrl(url))
      response <- ZIO.fromPromiseJS(Dom.window.fetch(url).asInstanceOf[js.Promise[dom.Response]])
      _        <- ZIO.fail(new RuntimeException(s"GET $url → ${response.status}")).when(!response.ok)
      body     <- ZIO.fromPromiseJS(response.text().asInstanceOf[js.Promise[String]])
      _        <- ZIO
        .fail(new RuntimeException(s"$url: body exceeds ${ProjectMeta.MaxBodyBytes} bytes"))
        .when(body.length > ProjectMeta.MaxBodyBytes)
      meta <- ZIO.fromEither(ProjectMeta.parseJson(body)).mapError(msg => new RuntimeException(s"$url: $msg"))
    yield meta.withSanitizedLinks
end ClientMain
