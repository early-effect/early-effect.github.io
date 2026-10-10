package earlyeffect.hub

import ascent.*
import ascent.css.Styles.*

/** Hub chrome authored as Ascent CSS: CssClass, Keyframes, GlobalStyle, typed media queries.
  *
  * Palette matches the Early Effect chalkboard tokens so the hub and library docs share a board.
  */
object HubCss:

  val bg: Color         = Color.hex("#1c1d1f")
  val surface: Color    = Color.hex("#2a2b2e")
  val ink: Color        = Color.hex("#e8e6dc")
  val muted: Color      = Color.hex("#c5c1b6")
  val terracotta: Color = Color.hex("#c46a52")
  val chalk: Color      = Color.hex("#d4a574")
  val rule: Color       = Color.hex("#3f4145")
  val cream: Color      = Color.hex("#e8e6dc")

  val lightBg: Color      = Color.hex("#d8d6ce")
  val lightSurface: Color = Color.hex("#e9e7df")
  val lightInk: Color     = Color.hex("#2e2f31")
  val lightMuted: Color   = Color.hex("#3f3d38")
  val lightAccent: Color  = Color.hex("#9c5848")
  val lightRule: Color    = Color.hex("#b5b3a8")

  private val lightProse: MediaQuery =
    MediaQuery(Media.prefersColorScheme.light, color(lightMuted))

  private val boardGrain: Image =
    Image.url(
      "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='560' height='560'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.22' numOctaves='2' stitchTiles='stitch'/%3E%3CfeGaussianBlur stdDeviation='3.4'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='0.16'/%3E%3C/svg%3E"
    )

  private val boardFill =
    Gradient.layers(
      Gradient.radial(RadialGeometry.circleAt(Position.at(18.pct, 8.pct)))(
        ColorStop(terracotta.alpha(0.16), Length.pct(0)),
        ColorStop(Color.transparent, Length.pct(42)),
      ),
      Gradient.radial(RadialGeometry.circleAt(Position.at(88.pct, 92.pct)))(
        ColorStop(chalk.alpha(0.10), Length.pct(0)),
        ColorStop(Color.transparent, Length.pct(48)),
      ),
      Gradient.linear(Angle.deg(180))(
        ColorStop(bg, Length.pct(0)),
        ColorStop(bg, Length.pct(100)),
      ),
    )

  object FadeUp
      extends Keyframes(
        Frame.from(
          opacity(0),
          transform(Transform.translateY(Length.px(12))),
        ),
        Frame.to(
          opacity(1),
          transform(Transform.translateY(Length.zero)),
        ),
      )

  private val stillMotion: MediaQuery = MediaQuery(
    Media.prefersReducedMotion.reduce,
    animation.none.important,
    transition.none.important,
    transform.none.important,
  )

  object Chrome
      extends GlobalStyle(
        Selector(
          Elem.html,
          scrollBehavior.smooth,
        ),
        Selector(
          PseudoClass.focusVisible,
          outline(Border.solid(2.px, terracotta)),
          outlineOffset.px(3),
        ),
        GlobalRule.atRule(
          "hub-reduced-motion",
          MediaQuery(
            Media.prefersReducedMotion.reduce,
            Selector(Elem.html, scrollBehavior.auto),
            Selector(
              Sel.universal
                .or(Sel.universal.pseudoElement(PseudoElement.before))
                .or(Sel.universal.pseudoElement(PseudoElement.after)),
              animationDuration.ms(0.01).important,
              transitionDuration.ms(0.01).important,
            ),
          ),
        ),
      )

  object Page
      extends CssClass(
        minHeight.vh(100),
        margin.zero,
        color(ink),
        background(boardFill),
        backgroundImage(boardGrain),
        backgroundSize.px(560),
        backgroundBlendMode.softLight,
        fontFamily.of(
          FontFamily.named("Avenir Next"),
          FontFamily.named("Avenir"),
          FontFamily.named("Segoe UI"),
          FontFamily.named("Helvetica Neue"),
          FontFamily.sansSerif,
        ),
        lineHeight(1.55),
        MediaQuery(
          Media.prefersColorScheme.light,
          color(lightInk),
          backgroundColor(lightBg),
        ),
      )

  object Nav
      extends CssClass(
        position.sticky,
        top.px(0),
        zIndex(30),
        display.flex,
        flexWrap.wrap,
        justifyContent.center,
        gap(0.85.rem),
        padding(0.55.rem, 1.rem),
        background(surface.alpha(0.86)),
        backdropFilter(Filter.blur(Length.px(12))),
        borderBottom(Border.solid(1.px, rule)),
        Selector(
          " a",
          color(muted),
          fontSize(0.82.rem),
          letterSpacing(0.12.em),
          textTransform.uppercase,
          textDecoration.none,
          fontWeight(600),
        ),
        Selector(" a:hover", color(terracotta), textDecoration.none),
        MediaQuery(
          Media.prefersColorScheme.light,
          background(lightSurface.alpha(0.92)),
          borderBottom(Border.solid(1.px, lightRule)),
          Selector(" a", color(lightMuted)),
        ),
      )

  object Hero
      extends CssClass(
        display.grid,
        gridTemplateColumns("96px minmax(0, 0.7fr) minmax(0, 1.35fr)"),
        gap(1.15.rem),
        alignItems.center,
        padding(1.05.rem, 2.rem, 0.15.rem, 2.rem),
        maxWidth.px(1120),
        margin(0.px, Length.auto),
        MediaQuery(
          Media.maxWidth.px(860),
          gridTemplateColumns("minmax(0, 1fr)"),
          padding(0.9.rem, 1.1.rem, 0.15.rem, 1.1.rem),
        ),
      )

  object Mark
      extends CssClass(
        display.block,
        width.auto,
        height.px(96),
        maxWidth.px(96),
      )

  object HeroCopy
      extends CssClass(
        minWidth.px(0)
      )

  object Title
      extends CssClass(
        fontSize(2.35.rem),
        fontWeight(800),
        letterSpacing((-0.03).em),
        lineHeight(1.05),
        margin.zero,
        FadeUp.use(Time.s(0.6), TimingFunction.easeOut, fill = Some(SingleAnimationFillMode.Both)),
        MediaQuery(Media.maxWidth.px(860), fontSize(2.rem)),
        stillMotion,
      )

  object Tagline
      extends CssClass(
        color(chalk),
        fontSize(1.05.rem),
        margin(0.45.rem, 0.px, 0.px, 0.px),
        fontWeight(600),
        MediaQuery(Media.prefersColorScheme.light, color(lightAccent)),
      )

  object Stack
      extends CssClass(
        maxWidth.px(1120),
        margin(0.px, Length.auto),
        padding(0.85.rem, 2.rem, 1.5.rem, 2.rem),
        MediaQuery(Media.maxWidth.px(860), padding(0.75.rem, 1.1.rem, 1.25.rem, 1.1.rem)),
      )

  object Bay
      extends CssClass(
        marginBottom(1.35.rem)
      )

  object BayHead
      extends CssClass(
        marginBottom(0.65.rem)
      )

  object BayTitle
      extends CssClass(
        fontSize(1.2.rem),
        fontWeight(800),
        margin.zero,
      )

  object BayThesis
      extends CssClass(
        color(muted),
        margin(0.15.rem, 0.px, 0.px, 0.px),
        lightProse,
      )

  object Grid
      extends CssClass(
        display.grid,
        gridTemplateColumns("repeat(auto-fill, minmax(min(100%, 16rem), 1fr))"),
        gap(0.75.rem),
      )

  object Card
      extends CssClass(
        display.flex,
        flexDirection.column,
        background(surface),
        backgroundImage(boardGrain),
        backgroundBlendMode.softLight,
        border(Border.solid(1.px, rule)),
        borderRadius.px(14),
        padding(0.9.rem, 1.rem, 0.85.rem, 1.rem),
        textDecoration.none,
        color(ink),
        transition(
          Transition.list(
            Transition("transform", Time.s(0.22), TimingFunction.easeOut),
            Transition("border-color", Time.s(0.22), TimingFunction.easeOut),
          )
        ),
        Selector(
          PseudoClass.hover,
          transform(Transform.translateY(Length.px(-3))),
          borderColor(terracotta),
          textDecoration.none,
          color(ink),
        ),
        MediaQuery(Media.prefersColorScheme.light, background(lightSurface), color(lightInk)),
        stillMotion,
      )

  object CardTitle
      extends CssClass(
        fontSize(1.05.rem),
        fontWeight(700),
        margin(0.px, 0.px, 0.25.rem, 0.px),
      )

  object CardBody
      extends CssClass(
        color(muted),
        fontSize(0.88.rem),
        margin.zero,
        flexGrow(1),
        lightProse,
      )

  object Meta
      extends CssClass(
        display.flex,
        flexWrap.wrap,
        gap(0.4.rem),
        marginTop(0.65.rem),
      )

  object Badge
      extends CssClass(
        fontSize(0.72.rem),
        color(muted),
        border(Border.solid(1.px, rule)),
        borderRadius.px(999),
        padding(0.12.rem, 0.55.rem),
        lightProse,
      )

  object Proof
      extends CssClass(
        display.grid,
        gridTemplateColumns("minmax(0, 1.15fr) minmax(0, 0.85fr)"),
        gap(0.85.rem),
        alignItems.start,
        MediaQuery(Media.maxWidth.px(860), gridTemplateColumns("minmax(0, 1fr)")),
      )

  object Board
      extends CssClass(
        background(Color.hex("#121314")),
        border(Border.solid(1.px, rule)),
        borderRadius.px(14),
        padding(0.85.rem, 0.95.rem),
        fontFamily.of(FontFamily.named("ui-monospace"), FontFamily.named("SFMono-Regular"), FontFamily.monospace),
        fontSize(0.82.rem),
        overflowX.auto,
        maxWidth.pct(100),
      )

  object BoardLabel
      extends CssClass(
        fontSize(0.68.rem),
        letterSpacing(0.14.em),
        textTransform.uppercase,
        fontWeight(700),
        margin(0.px, 0.px, 0.35.rem, 0.px),
        color(cream),
      )

  object Pass
      extends CssClass(
        color(Color.hex("#b7c9a3")),
        whiteSpace.preWrap,
        margin(0.px, 0.px, 0.75.rem, 0.px),
      )

  object Fail
      extends CssClass(
        color(terracotta.lighten(0.12)),
        whiteSpace.preWrap,
        margin.zero,
        opacity(0.85),
      )

  object ProofNote
      extends CssClass(
        color(muted),
        margin.zero,
        fontSize(0.92.rem),
        lightProse,
      )

  object ProofNav
      extends CssClass(
        display.flex,
        flexWrap.wrap,
        gap(0.5.rem),
        marginTop(0.65.rem),
      )

  object Ghost
      extends CssClass(
        background(surface),
        color(ink),
        border(Border.solid(1.px, rule)),
        borderRadius.px(10),
        padding(0.35.rem, 0.8.rem),
        fontWeight(600),
        cursor.pointer,
        Selector(PseudoClass.hover, borderColor(terracotta), color(terracotta)),
      )

  object Footer
      extends CssClass(
        padding(0.9.rem, 1.25.rem, 1.4.rem, 1.25.rem),
        borderTop(Border.solid(1.px, rule)),
        color(muted),
        fontSize(0.92.rem),
        textAlign.center,
        Selector(" a", color(chalk), textDecoration.none, fontWeight(700)),
        Selector(" a:hover", color(terracotta)),
        lightProse,
        MediaQuery(
          Media.prefersColorScheme.light,
          Selector(" a", color(lightAccent)),
          Selector(" a:hover", color(lightInk)),
        ),
      )

  object MakerLine
      extends CssClass(
        margin(0.px, 0.px, 0.35.rem, 0.px)
      )

  object MakerName
      extends CssClass(
        color(ink),
        fontWeight(800),
        MediaQuery(Media.prefersColorScheme.light, color(lightInk)),
      )

  object MakerLinks
      extends CssClass(
        margin(0.px, 0.px, 0.45.rem, 0.px)
      )

  object Colophon
      extends CssClass(
        margin.zero,
        fontSize(0.82.rem),
      )
end HubCss
