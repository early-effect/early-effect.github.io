package earlyeffect.hub

import ascent.ast.{Attr, UI}
import ascent.domtypes.AttrValue
import specular.site.*
import zio.*

/** SSR the designed landing. JS only hydrates catalog grids and the proof flipper. */
final class HubLanding extends LandingTemplate:

  def wrap(model: SiteModel): Task[UI[Any]] =
    val home     = model.home.getOrElse(HomePage())
    val catalog  = home.sections.collectFirst { case c: ProjectCatalog => c }
    val projects = catalog.map(_.projects).getOrElse(Vector.empty)
    val urls     = catalog.map(_.metadataUrls).getOrElse(Vector.empty)
    ZIO.succeed(
      el(
        "html",
        Vector(head(model, urls), HubView.body(projects)),
        Vector(attr("lang", "en")),
      )
    )
  end wrap

  private def head(model: SiteModel, urls: Vector[String]): UI[Any] =
    val desc = model.description.getOrElse(HubCopy.description)
    el(
      "head",
      Vector(
        el("meta", Vector.empty, Vector(attr("charset", "utf-8"))),
        el(
          "meta",
          Vector.empty,
          Vector(attr("name", "viewport"), attr("content", "width=device-width, initial-scale=1")),
        ),
        el("title", Vector(UI.Text(model.title))),
        el("meta", Vector.empty, Vector(attr("name", "description"), attr("content", desc))),
        el("meta", Vector.empty, Vector(attr("property", "og:title"), attr("content", HubCopy.title))),
        el("meta", Vector.empty, Vector(attr("property", "og:description"), attr("content", desc))),
        el("meta", Vector.empty, Vector(attr("property", "og:type"), attr("content", "website"))),
        el("meta", Vector.empty, Vector(attr("property", "og:url"), attr("content", HubCopy.origin))),
        el(
          "meta",
          Vector.empty,
          Vector(attr("property", "og:image"), attr("content", s"${HubCopy.origin}/${HubCopy.heroImage}")),
        ),
        el("meta", Vector.empty, Vector(attr("name", "twitter:card"), attr("content", "summary_large_image"))),
        el(
          "link",
          Vector.empty,
          Vector(attr("rel", "icon"), attr("href", "images/logo.png"), attr("type", "image/png")),
        ),
        el("link", Vector.empty, Vector(attr("rel", "stylesheet"), attr("href", "assets/theme.css"))),
        el("link", Vector.empty, Vector(attr("rel", "stylesheet"), attr("href", "assets/index.css"))),
      ) ++ model.clientScript.toVector.flatMap { src =>
        SafeHref.sanitizeClientScript(src).toVector.map { safe =>
          el("script", Vector.empty, Vector(attr("src", safe), attr("defer", "defer")))
        }
      } ++ urls.filter(ProjectMeta.isAllowedMetaUrl).map { url =>
        el("link", Vector.empty, Vector(attr("rel", LiveCatalogIds.MetaLinkRel), attr("href", url)))
      },
    )
  end head

  private def el(tag: String, children: Vector[UI[Any]], attrs: Vector[Attr[Any]] = Vector.empty): UI[Any] =
    UI.Element(tag, attrs, children)

  private def attr(name: String, value: String): Attr[Any] =
    Attr.StaticAttr(name, AttrValue.Str(value))
end HubLanding

object HubLanding:
  val live: ULayer[LandingTemplate] =
    ZLayer.succeed(new HubLanding)
