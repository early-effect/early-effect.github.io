package earlyeffect.hub

import ascent.*
import ascent.dsl.*
import specular.site.ProjectMeta

/** Static Ascent tree for the hub. JVM SSRs it; JS only remounts live islands. */
object HubView:

  val ProofMountId = "hub-proof"

  def catalogMountId(layerId: String): String = s"hub-catalog-$layerId"

  def body(projects: Vector[ProjectMeta]): UI[Any] =
    E.body(
      HubCss.Page,
      HubCss.Chrome,
      Aria.ariaLabel("Early Effect"),
      nav,
      hero,
      stack(projects),
      footer,
    )

  def card(project: ProjectMeta): UI[Any] =
    val safe   = project.withSanitizedLinks
    val href   = safe.docsUrl.orElse(safe.homepage).getOrElse("#")
    val badges = safe.versionBadge.toVector ++ safe.language.toVector
    val title  = E.h3(HubCss.CardTitle, safe.displayTitle)
    val blurb  = E.p(HubCss.CardBody, CatalogGroups.blurb(safe))
    val meta   = E.div(HubCss.Meta, fragment(badges.map(b => E.span(HubCss.Badge, b))*))
    if HubCopy.sameOrigin(href) then E.a(HubCss.Card, A.href(href), title, blurb, meta)
    else
      E.a(
        HubCss.Card,
        A.href(href),
        A.rel("noopener noreferrer"),
        A.target("_blank"),
        title,
        blurb,
        meta,
      )

  def proofPanel(beat: HubCopy.ProofBeat): UI[Any] =
    E.div(
      HubCss.Proof,
      E.div(
        HubCss.Board,
        E.p(HubCss.BoardLabel, s"${beat.label} · this compiles"),
        E.pre(HubCss.Pass, beat.compiles),
        E.p(HubCss.BoardLabel, "this does not"),
        E.pre(HubCss.Fail, beat.fails),
      ),
      E.p(HubCss.ProofNote, beat.note),
    )

  private def nav: UI[Any] =
    E.nav(
      HubCss.Nav,
      Aria.ariaLabel("On this page"),
      fragment(HubCopy.nav.map { (label, href) =>
        E.a(A.href(href), label)
      }*),
    )

  private def hero: UI[Any] =
    E.header(
      HubCss.Hero,
      E.img(
        HubCss.Mark,
        A.src(HubCopy.heroImage),
        A.alt(HubCopy.markAlt),
        A.width(72),
        A.height(96),
      ),
      E.div(
        HubCss.HeroCopy,
        E.h1(HubCss.Title, HubCopy.title),
        E.p(HubCss.Tagline, HubCopy.tagline),
      ),
      proof,
    )

  private def proof: UI[Any] =
    val first = HubCopy.proofBeats match
      case beat +: _ => proofPanel(beat)
      case _         => E.p(HubCss.ProofNote, "")
    E.div(
      A.id("proof"),
      E.div(A.id(ProofMountId), first),
    )

  private def stack(projects: Vector[ProjectMeta]): UI[Any] =
    val grouped = CatalogGroups.bays(projects)
    E.section(
      HubCss.Stack,
      fragment(grouped.map { bay =>
        E.div(
          HubCss.Bay,
          A.id(bay.layer.id),
          E.div(
            HubCss.BayHead,
            E.h2(HubCss.BayTitle, bay.layer.title),
            E.p(HubCss.BayThesis, bay.layer.thesis),
          ),
          E.div(
            HubCss.Grid,
            A.id(catalogMountId(bay.layer.id)),
            if bay.projects.isEmpty then
              E.p(HubCss.BayThesis, s"${bay.layer.title} libraries publish here as they land.")
            else fragment(bay.projects.map(card)*),
          ),
        )
      }*),
    )

  private def footer: UI[Any] =
    E.footer(
      HubCss.Footer,
      E.p(
        HubCss.MakerLine,
        E.span(HubCss.MakerName, HubCopy.makerName),
        E.span(". "),
        E.span(HubCopy.makerLine),
      ),
      E.p(
        HubCss.MakerLinks,
        externalLink(HubCopy.githubUser, "GitHub"),
        E.span(" · "),
        externalLink(HubCopy.xProfile, "X"),
      ),
      E.p(
        HubCss.Colophon,
        E.span("Apache-2.0 · "),
        E.a(A.href("https://www.earlyeffect.rocks/specular/"), "specular"),
        E.span(" + "),
        E.a(A.href("https://www.earlyeffect.rocks/ascent/"), "ascent"),
      ),
    )

  private def externalLink(href: String, label: String): UI[Any] =
    E.a(A.href(href), A.rel("noopener noreferrer"), A.target("_blank"), label)
end HubView
