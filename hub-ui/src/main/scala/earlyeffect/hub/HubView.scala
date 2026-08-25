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
      marquee,
      craft,
      machine(projects),
      proof,
      maker,
      footer,
    )

  def card(project: ProjectMeta): UI[Any] =
    val safe   = project.withSanitizedLinks
    val href   = safe.docsUrl.orElse(safe.homepage).getOrElse("#")
    val badges = safe.versionBadge.toVector ++ safe.language.toVector
    E.a(
      HubCss.Card,
      A.href(href),
      A.rel("noopener noreferrer"),
      A.target("_blank"),
      E.h3(HubCss.CardTitle, safe.displayTitle),
      E.p(HubCss.CardBody, CatalogGroups.blurb(safe)),
      E.div(HubCss.Meta, fragment(badges.map(b => E.span(HubCss.Badge, b))*)),
    )
  end card

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
      E.div(
        HubCss.MarkWrap,
        E.div(
          HubCss.MoteLayer,
          Aria.ariaHidden(true),
          fragment((1 to 8).toVector.map(_ => E.span(HubCss.Mote, ""))*),
        ),
        E.img(
          HubCss.Mark,
          A.src(HubCopy.heroImage),
          A.alt(HubCopy.title),
          A.height(420),
        ),
      ),
      E.div(
        HubCss.HeroCopy,
        E.p(HubCss.Kicker, "Act I"),
        E.p(HubCss.Eyebrow, "the board"),
        E.h1(HubCss.Title, HubCopy.title),
        E.p(HubCss.Tagline, HubCopy.tagline),
        E.p(HubCss.Manifesto, HubCopy.manifesto),
        E.div(
          HubCss.Ctas,
          E.a(HubCss.CtaAccent, A.href(HubCopy.githubOrg), A.rel("noopener noreferrer"), A.target("_blank"), "GitHub"),
          E.a(
            HubCss.Cta,
            A.href(HubCopy.mavenCentral),
            A.rel("noopener noreferrer"),
            A.target("_blank"),
            "Maven Central",
          ),
          E.a(HubCss.Cta, A.href(HubCopy.xProfile), A.rel("noopener noreferrer"), A.target("_blank"), "@russwyte"),
        ),
      ),
    )

  private def marquee: UI[Any] =
    val items = HubCopy.slogans ++ HubCopy.slogans
    E.div(
      HubCss.MarqueeBand,
      Aria.ariaHidden(true),
      E.div(HubCss.MarqueeTrack, fragment(items.map(s => E.span(HubCss.MarqueeItem, s))*)),
    )

  private def craft: UI[Any] =
    E.section(
      HubCss.Section,
      HubCss.Reveal,
      A.id("craft"),
      E.p(HubCss.Kicker, "Act II"),
      E.h2(HubCss.Heading, "Four rules on the board"),
      E.p(HubCss.Lead, "Craft is what still holds after the clever bit has worn off."),
      E.div(
        HubCss.RuleGrid,
        fragment(HubCopy.rules.zipWithIndex.map { (rule, i) =>
          E.article(
            HubCss.Rule,
            HubCss.Reveal,
            A.style(s"animation-delay: ${0.08 * i}s"),
            E.p(HubCss.RuleNum, rule.n),
            E.h3(HubCss.RuleTitle, rule.title),
            E.p(HubCss.RuleBody, rule.body),
            E.div(HubCss.Ticks, fragment(rule.ticks.map(t => E.span(HubCss.Tick, t))*)),
          )
        }*),
      ),
    )

  private def machine(projects: Vector[ProjectMeta]): UI[Any] =
    val grouped = CatalogGroups.bays(projects)
    E.section(
      HubCss.Section,
      A.id("stack"),
      E.p(HubCss.Kicker, "Act III"),
      E.h2(HubCss.Heading, "The machine"),
      E.p(
        HubCss.Lead,
        "Write, prove, ship. One vertical. Cards refresh from live metadata when JavaScript runs.",
      ),
      fragment(grouped.map { bay =>
        E.div(
          HubCss.Bay,
          HubCss.Reveal,
          E.div(
            HubCss.BayHead,
            E.h3(HubCss.BayTitle, bay.layer.title),
            E.p(HubCss.BayThesis, bay.layer.thesis),
          ),
          E.div(
            HubCss.Pins,
            Aria.ariaHidden(true),
            E.span(HubCss.Pin, ""),
            E.span(HubCss.Pin, ""),
            E.span(HubCss.Pin, ""),
          ),
          E.div(
            HubCss.Grid,
            A.id(catalogMountId(bay.layer.id)),
            if bay.projects.isEmpty then E.p(HubCss.Lead, s"${bay.layer.title} libraries publish here as they land.")
            else fragment(bay.projects.map(card)*),
          ),
        )
      }*),
    )
  end machine

  private def proof: UI[Any] =
    E.section(
      HubCss.Section,
      HubCss.Reveal,
      A.id("proof"),
      E.p(HubCss.Kicker, "Act IV"),
      E.h2(HubCss.Heading, "It runs"),
      E.p(HubCss.Lead, HubCopy.proofCaption),
      E.div(A.id(ProofMountId), proofPanel(HubCopy.proofBeats.head)),
    )

  private def maker: UI[Any] =
    E.section(
      HubCss.Section,
      HubCss.Reveal,
      A.id("maker"),
      E.p(HubCss.Kicker, "Act V"),
      E.h2(HubCss.Heading, HubCopy.makerName),
      E.div(
        HubCss.Maker,
        E.img(HubCss.MakerMark, A.src(HubCopy.heroImage), A.alt("Early Effect mark")),
        E.div(
          E.p(HubCss.Bio, HubCopy.makerBio),
          E.div(
            HubCss.Ctas,
            E.a(
              HubCss.Cta,
              A.href(HubCopy.githubUser),
              A.rel("noopener noreferrer"),
              A.target("_blank"),
              "github.com/russwyte",
            ),
            E.a(HubCss.Cta, A.href(HubCopy.xProfile), A.rel("noopener noreferrer"), A.target("_blank"), "X"),
            E.a(
              HubCss.Cta,
              A.href(HubCopy.githubOrg),
              A.rel("noopener noreferrer"),
              A.target("_blank"),
              "early-effect",
            ),
          ),
        ),
      ),
    )

  private def footer: UI[Any] =
    E.footer(
      HubCss.Footer,
      E.span("Apache-2.0 · "),
      E.a(
        A.href("https://www.earlyeffect.rocks/specular/"),
        A.rel("noopener noreferrer"),
        A.target("_blank"),
        "specular",
      ),
      E.span(" + "),
      E.a(A.href("https://www.earlyeffect.rocks/ascent/"), A.rel("noopener noreferrer"), A.target("_blank"), "ascent"),
    )
end HubView
