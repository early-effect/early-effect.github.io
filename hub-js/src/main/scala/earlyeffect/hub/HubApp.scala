package earlyeffect.hub

import ascent.*
import ascent.dsl.*
import specular.site.ProjectMeta
import zio.*

/** Five-act Early Effect landing, authored as an Ascent program. */
object HubApp:

  def body(projects: Vector[ProjectMeta]): UIO[UI[Any]] =
    for beat <- sq(0)
    yield E.body(
      HubCss.Page,
      HubCss.Chrome,
      Aria.ariaLabel("Early Effect"),
      nav,
      hero,
      marquee,
      craft,
      machine(projects),
      proof(beat),
      maker,
      footer,
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
        "Write, prove, ship. One vertical. The cards fetch live version metadata from each micro-site.",
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
            if bay.projects.isEmpty then E.p(HubCss.Lead, s"${bay.layer.title} libraries publish here as they land.")
            else fragment(bay.projects.map(card)*),
          ),
        )
      }*),
    )
  end machine

  private def card(project: ProjectMeta): UI[Any] =
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

  private def proof(beat: Source[Int]): UI[Any] =
    val n       = HubCopy.proofBeats.size
    val current = beat.map(i => HubCopy.proofBeats(Math.floorMod(i, n)))
    E.section(
      HubCss.Section,
      HubCss.Reveal,
      A.id("proof"),
      E.p(HubCss.Kicker, "Act IV"),
      E.h2(HubCss.Heading, "It runs"),
      E.p(HubCss.Lead, HubCopy.proofCaption),
      E.div(
        HubCss.Proof,
        E.div(
          HubCss.Board,
          E.p(HubCss.BoardLabel, current.map(b => s"${b.label} · this compiles")),
          E.pre(HubCss.Pass, current.map(_.compiles)),
          E.p(HubCss.BoardLabel, "this does not"),
          E.pre(HubCss.Fail, current.map(_.fails)),
        ),
        E.div(
          E.p(HubCss.ProofNote, current.map(_.note)),
          E.div(
            HubCss.ProofNav,
            E.button(
              HubCss.Ghost,
              Ev.onClick(_ => beat.update(i => Math.floorMod(i - 1, n))),
              "Previous",
            ),
            E.button(
              HubCss.Ghost,
              Ev.onClick(_ => beat.update(i => Math.floorMod(i + 1, n))),
              "Next",
            ),
          ),
        ),
      ),
    )
  end proof

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

end HubApp
