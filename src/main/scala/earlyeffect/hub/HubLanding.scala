package earlyeffect.hub

import ascent.ast.{Attr, UI}
import ascent.domtypes.AttrValue
import specular.site.*
import zio.*

/** SSR shell: head chrome + no-JS fallback. The Ascent SPA replaces `body` on load. */
final class HubLanding(theme: Theme) extends LandingTemplate:

  def wrap(model: SiteModel): Task[UI[Any]] =
    val home     = model.home.getOrElse(HomePage())
    val catalog  = home.sections.collectFirst { case c: ProjectCatalog => c }
    val projects = catalog.map(_.projects).getOrElse(Vector.empty)
    val urls     = catalog.map(_.metadataUrls).getOrElse(Vector.empty)
    theme.classNames.map { classes =>
      el(
        "html",
        Vector(head(model, urls), body(classes, projects)),
        Vector(attr("lang", "en")),
      )
    }
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

  private def body(classes: ThemeClasses, projects: Vector[ProjectMeta]): UI[Any] =
    val bays = CatalogGroups.bays(projects)
    el(
      "body",
      Vector(
        el(
          "header",
          Vector(
            el(
              "img",
              Vector.empty,
              Vector(
                attr("class", "specular-hero-image"),
                attr("src", HubCopy.heroImage),
                attr("alt", HubCopy.title),
                attr("height", "160"),
              ),
            ),
            el("h1", Vector(UI.Text(HubCopy.title)), Vector(attr("class", "specular-hero-title"))),
            el("p", Vector(UI.Text(HubCopy.tagline)), Vector(attr("class", "specular-hero-subtitle"))),
            el("p", Vector(UI.Text(HubCopy.manifesto))),
            el(
              "nav",
              Vector(
                a("GitHub", HubCopy.githubOrg),
                a("Maven Central", HubCopy.mavenCentral),
                a("@russwyte", HubCopy.xProfile),
              ),
              Vector(attr("class", "specular-hero-links")),
            ),
          ),
          Vector(attr("class", classes.hero)),
        ),
        el(
          "section",
          Vector(el("h2", Vector(UI.Text("Four rules on the board")))) ++
            HubCopy.rules.map { r =>
              el(
                "article",
                Vector(
                  el("h3", Vector(UI.Text(r.title))),
                  el("p", Vector(UI.Text(r.body))),
                ),
              )
            },
        ),
      ) ++ bays.map { bay =>
        el(
          "section",
          Vector(
            el("h2", Vector(UI.Text(bay.layer.title)), Vector(attr("class", "specular-catalog-heading"))),
            el("p", Vector(UI.Text(bay.layer.thesis))),
            CatalogCards.grid(bay.projects, classes.card),
          ),
          Vector(attr("class", classes.catalog)),
        )
      } ++ Vector(
        el(
          "section",
          Vector(
            el("h2", Vector(UI.Text(HubCopy.makerName))),
            el("p", Vector(UI.Text(HubCopy.makerBio))),
          ),
        ),
        el(
          "footer",
          BuiltWith.credit(Some("Early Effect")),
          Vector(attr("class", classes.footer)),
        ),
      ),
      Vector(attr("class", classes.landing)),
    )
  end body

  private def a(label: String, href: String): UI[Any] =
    el("a", Vector(UI.Text(label)), SafeHref.anchorAttrs(href).map { case (k, v) => attr(k, v) })

  private def el(tag: String, children: Vector[UI[Any]], attrs: Vector[Attr[Any]] = Vector.empty): UI[Any] =
    UI.Element(tag, attrs, children)

  private def attr(name: String, value: String): Attr[Any] =
    Attr.StaticAttr(name, AttrValue.Str(value))
end HubLanding

object HubLanding:
  val live: ZLayer[Theme, Nothing, LandingTemplate] =
    ZLayer.fromFunction(new HubLanding(_))
