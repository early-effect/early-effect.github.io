package earlyeffect.hub

import ascent.*
import ascent.css.Styles.*

/** Hub chrome authored as Ascent CSS: CssClass, Keyframes, GlobalStyle, typed media queries.
  *
  * Palette matches the Early Effect chalkboard tokens so the SPA and library docs share a board.
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
      "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='480' height='480'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.32' numOctaves='2' stitchTiles='stitch'/%3E%3CfeGaussianBlur stdDeviation='2.2'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='0.28'/%3E%3C/svg%3E"
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

  object FloatMark
      extends Keyframes(
        Frame.from(transform(Transform.translateY(Length.zero))),
        Frame.pct(50)(transform(Transform.translateY(Length.px(-10)))),
        Frame.to(transform(Transform.translateY(Length.zero))),
      )

  object Drift
      extends Keyframes(
        Frame.from(
          transform(Transform.list(Transform.translateY(Length.zero), Transform.scale(1))),
          opacity(0.15),
        ),
        Frame.pct(40)(opacity(0.7)),
        Frame.to(
          transform(Transform.list(Transform.translateY(Length.px(-90)), Transform.scale(0.4))),
          opacity(0),
        ),
      )

  object Marquee
      extends Keyframes(
        Frame.from(transform(Transform.translateX(Length.zero))),
        Frame.to(transform(Transform.translateX(Length.pct(-50)))),
      )

  object FadeUp
      extends Keyframes(
        Frame.from(
          opacity(0),
          transform(Transform.translateY(Length.px(22))),
        ),
        Frame.to(
          opacity(1),
          transform(Transform.translateY(Length.zero)),
        ),
      )

  object PulsePin
      extends Keyframes(
        Frame.from(transform(Transform.scale(1)), opacity(0.85)),
        Frame.pct(50)(transform(Transform.scale(1.35)), opacity(0.35)),
        Frame.to(transform(Transform.scale(1)), opacity(0.85)),
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
        backgroundSize.px(480),
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
          background(lightBg),
        ),
      )

  object Nav
      extends CssClass(
        position.sticky,
        top.px(0),
        zIndex(30),
        display.flex,
        justifyContent.center,
        gap(1.25.rem),
        padding(0.7.rem, 1.25.rem),
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
        position.relative,
        overflow.hidden,
        display.grid,
        gridTemplateColumns("1.05fr 1fr"),
        gap(2.5.rem),
        alignItems.center,
        padding(4.5.rem, 2.rem, 3.rem, 2.rem),
        maxWidth.px(1120),
        margin(0.px, Length.auto),
        MediaQuery(
          Media.maxWidth.px(900),
          gridTemplateColumns("1fr"),
          textAlign.center,
          padding(3.rem, 1.25.rem, 2.rem, 1.25.rem),
        ),
      )

  object MarkWrap
      extends CssClass(
        position.relative,
        display.flex,
        justifyContent.center,
        FloatMark.use(
          Time.s(5.5),
          TimingFunction.easeInOut,
          iterations = Some(SingleAnimationIterationCount.Infinite),
        ),
        stillMotion,
      )

  object MoteLayer
      extends CssClass(
        position.absolute,
        inset.zero,
        pointerEvents.none,
        overflow.hidden,
      )

  object Mark
      extends CssClass(
        display.block,
        width.pct(78),
        maxWidth.px(420),
        height.auto,
        filter(
          Filter.dropShadow(Shadow(Length.zero, Length.px(18), Length.px(40), terracotta.alpha(0.28)))
        ),
        MediaQuery(Media.maxWidth.px(900), margin(0.px, Length.auto), width.pct(62)),
      )

  object Mote
      extends CssClass(
        position.absolute,
        width.px(5),
        height.px(5),
        borderRadius.pct(50),
        background(cream.alpha(0.55)),
        pointerEvents.none,
        Drift.use(
          Time.s(9),
          TimingFunction.linear,
          iterations = Some(SingleAnimationIterationCount.Infinite),
        ),
        Selector(":nth-child(1)", left.pct(12), top.pct(72), animationDelay.s(0)),
        Selector(":nth-child(2)", left.pct(28), top.pct(58), animationDelay.s(1.4), background(terracotta.alpha(0.7))),
        Selector(":nth-child(3)", left.pct(44), top.pct(80), animationDelay.s(2.8)),
        Selector(":nth-child(4)", left.pct(61), top.pct(64), animationDelay.s(0.7), background(chalk.alpha(0.65))),
        Selector(":nth-child(5)", left.pct(74), top.pct(78), animationDelay.s(3.3)),
        Selector(":nth-child(6)", left.pct(18), top.pct(86), animationDelay.s(4.1), background(terracotta.alpha(0.45))),
        Selector(":nth-child(7)", left.pct(86), top.pct(54), animationDelay.s(2.1)),
        Selector(":nth-child(8)", left.pct(52), top.pct(48), animationDelay.s(5.2), background(cream.alpha(0.4))),
        stillMotion,
      )

  object HeroCopy
      extends CssClass(
        MediaQuery(Media.maxWidth.px(900), Selector(" *", marginLeft.auto, marginRight.auto))
      )

  object Eyebrow
      extends CssClass(
        color(terracotta),
        fontSize(0.78.rem),
        letterSpacing(0.22.em),
        textTransform.uppercase,
        fontWeight(700),
        margin(0.px, 0.px, 0.75.rem, 0.px),
      )

  object Title
      extends CssClass(
        fontSize(3.35.rem),
        fontWeight(800),
        letterSpacing((-0.03).em),
        lineHeight(1.05),
        margin.zero,
        FadeUp.use(Time.s(0.8), TimingFunction.easeOut, fill = Some(SingleAnimationFillMode.Both)),
        MediaQuery(Media.maxWidth.px(900), fontSize(2.45.rem)),
        stillMotion,
      )

  object Tagline
      extends CssClass(
        color(chalk),
        fontSize(1.28.rem),
        margin(0.85.rem, 0.px, 0.px, 0.px),
        fontWeight(600),
        MediaQuery(Media.prefersColorScheme.light, color(lightAccent)),
      )

  object Manifesto
      extends CssClass(
        color(muted),
        fontSize(1.05.rem),
        maxWidth.px(36 * 16),
        margin(1.15.rem, 0.px, 0.px, 0.px),
        lightProse,
      )

  object Ctas
      extends CssClass(
        display.flex,
        flexWrap.wrap,
        gap(0.75.rem),
        marginTop(1.6.rem),
      )

  object Cta
      extends CssClass(
        display.inlineBlock,
        padding(0.55.rem, 1.15.rem),
        border(Border.solid(1.px, rule)),
        borderRadius.px(12),
        color(ink),
        fontWeight(600),
        textDecoration.none,
        background(surface),
        Selector(PseudoClass.hover, borderColor(terracotta), color(terracotta), textDecoration.none),
        MediaQuery(Media.prefersColorScheme.light, color(lightInk), background(lightSurface)),
      )

  object CtaAccent
      extends CssClass(
        display.inlineBlock,
        padding(0.55.rem, 1.15.rem),
        border(Border.solid(1.px, terracotta)),
        borderRadius.px(12),
        color(bg),
        fontWeight(700),
        textDecoration.none,
        background(terracotta),
        Selector(PseudoClass.hover, background(terracotta.lighten(0.08)), textDecoration.none, color(bg)),
      )

  object MarqueeBand
      extends CssClass(
        overflow.hidden,
        borderTop(Border.solid(1.px, rule)),
        borderBottom(Border.solid(1.px, rule)),
        padding(0.65.rem, 0.px),
        color(muted),
        fontSize(0.78.rem),
        letterSpacing(0.16.em),
        textTransform.uppercase,
        lightProse,
      )

  object MarqueeTrack
      extends CssClass(
        display.flex,
        width.maxContent,
        Marquee.use(
          Time.s(38),
          TimingFunction.linear,
          iterations = Some(SingleAnimationIterationCount.Infinite),
        ),
        stillMotion,
      )

  object MarqueeItem
      extends CssClass(
        padding(0.px, 1.75.rem),
        whiteSpace.nowrap,
        Selector("::after", content("  ·"), color(terracotta)),
      )

  object Section
      extends CssClass(
        maxWidth.px(1120),
        margin(0.px, Length.auto),
        padding(3.5.rem, 2.rem),
        MediaQuery(Media.maxWidth.px(900), padding(2.5.rem, 1.25.rem)),
      )

  object Kicker
      extends CssClass(
        color(terracotta),
        fontSize(0.75.rem),
        letterSpacing(0.18.em),
        textTransform.uppercase,
        fontWeight(700),
        margin.zero,
      )

  object Heading
      extends CssClass(
        fontSize(2.rem),
        fontWeight(800),
        letterSpacing((-0.02).em),
        margin(0.4.rem, 0.px, 0.75.rem, 0.px),
        MediaQuery(Media.maxWidth.px(900), fontSize(1.55.rem)),
      )

  object Lead
      extends CssClass(
        color(muted),
        maxWidth.px(40 * 16),
        margin(0.px, 0.px, 2.rem, 0.px),
        lightProse,
      )

  object Reveal
      extends CssClass(
        FadeUp.use(
          Time.s(0.7),
          TimingFunction.easeOut,
          fill = Some(SingleAnimationFillMode.Both),
        ),
        stillMotion,
      )

  object RuleGrid
      extends CssClass(
        display.grid,
        gridTemplateColumns("repeat(2, minmax(0, 1fr))"),
        gap(1.1.rem),
        MediaQuery(Media.maxWidth.px(720), gridTemplateColumns("1fr")),
      )

  object Rule
      extends CssClass(
        background(surface),
        backgroundImage(boardGrain),
        backgroundBlendMode.softLight,
        border(Border.solid(1.px, rule)),
        borderRadius.px(14),
        padding(1.35.rem, 1.4.rem, 1.2.rem, 1.4.rem),
        borderLeft(Border.solid(3.px, terracotta)),
        Selector(PseudoClass.hover, borderColor(terracotta)),
        MediaQuery(Media.prefersColorScheme.light, background(lightSurface)),
      )

  object RuleNum
      extends CssClass(
        color(terracotta),
        fontWeight(800),
        letterSpacing(0.14.em),
        fontSize(0.8.rem),
        margin.zero,
      )

  object RuleTitle
      extends CssClass(
        fontSize(1.2.rem),
        fontWeight(700),
        margin(0.35.rem, 0.px, 0.45.rem, 0.px),
      )

  object RuleBody
      extends CssClass(
        color(muted),
        margin.zero,
        fontSize(0.95.rem),
        lightProse,
      )

  object Ticks
      extends CssClass(
        display.flex,
        flexWrap.wrap,
        gap(0.4.rem),
        marginTop(0.9.rem),
      )

  object Tick
      extends CssClass(
        fontSize(0.72.rem),
        letterSpacing(0.06.em),
        textTransform.uppercase,
        color(chalk),
        border(Border.solid(1.px, rule)),
        borderRadius.px(999),
        padding(0.15.rem, 0.6.rem),
      )

  object Bay
      extends CssClass(
        marginBottom(2.4.rem)
      )

  object BayHead
      extends CssClass(
        display.flex,
        alignItems.baseline,
        gap(0.85.rem),
        marginBottom(1.rem),
        MediaQuery(Media.maxWidth.px(720), flexDirection.column, gap(0.25.rem)),
      )

  object BayTitle
      extends CssClass(
        fontSize(1.35.rem),
        fontWeight(800),
        margin.zero,
      )

  object BayThesis
      extends CssClass(
        color(muted),
        margin.zero,
        lightProse,
      )

  object Pins
      extends CssClass(
        display.flex,
        gap(0.45.rem),
        margin(0.35.rem, 0.px, 0.9.rem, 0.px),
      )

  object Pin
      extends CssClass(
        width.px(9),
        height.px(9),
        borderRadius.pct(50),
        background(terracotta),
        PulsePin.use(
          Time.s(2.4),
          TimingFunction.easeInOut,
          iterations = Some(SingleAnimationIterationCount.Infinite),
        ),
        Selector(":nth-child(2)", animationDelay.s(0.35), background(chalk)),
        Selector(":nth-child(3)", animationDelay.s(0.7), background(cream.alpha(0.85))),
        stillMotion,
      )

  object Grid
      extends CssClass(
        display.grid,
        gridTemplateColumns("repeat(auto-fill, minmax(16.5rem, 1fr))"),
        gap(1.rem),
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
        padding(1.2.rem, 1.25.rem, 1.1.rem, 1.25.rem),
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
          transform(Transform.translateY(Length.px(-4))),
          borderColor(terracotta),
          textDecoration.none,
          color(ink),
        ),
        MediaQuery(Media.prefersColorScheme.light, background(lightSurface), color(lightInk)),
        stillMotion,
      )

  object CardTitle
      extends CssClass(
        fontSize(1.12.rem),
        fontWeight(700),
        margin(0.px, 0.px, 0.35.rem, 0.px),
      )

  object CardBody
      extends CssClass(
        color(muted),
        fontSize(0.92.rem),
        margin.zero,
        flexGrow(1),
        lightProse,
      )

  object Meta
      extends CssClass(
        display.flex,
        flexWrap.wrap,
        gap(0.4.rem),
        marginTop(0.85.rem),
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
        gridTemplateColumns("1.1fr 1fr"),
        gap(1.5.rem),
        alignItems.start,
        MediaQuery(Media.maxWidth.px(800), gridTemplateColumns("1fr")),
      )

  object Board
      extends CssClass(
        background(Color.hex("#121314")),
        border(Border.solid(1.px, rule)),
        borderRadius.px(14),
        padding(1.25.rem, 1.3.rem),
        fontFamily.of(FontFamily.named("ui-monospace"), FontFamily.named("SFMono-Regular"), FontFamily.monospace),
        fontSize(0.88.rem),
        overflowX.auto,
      )

  object BoardLabel
      extends CssClass(
        fontSize(0.68.rem),
        letterSpacing(0.14.em),
        textTransform.uppercase,
        fontWeight(700),
        margin(0.px, 0.px, 0.45.rem, 0.px),
      )

  object Pass
      extends CssClass(
        color(Color.hex("#b7c9a3")),
        whiteSpace.preWrap,
        margin(0.px, 0.px, 1.1.rem, 0.px),
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
        lightProse,
      )

  object ProofNav
      extends CssClass(
        display.flex,
        gap(0.6.rem),
        marginTop(1.1.rem),
      )

  object Ghost
      extends CssClass(
        background(surface),
        color(ink),
        border(Border.solid(1.px, rule)),
        borderRadius.px(10),
        padding(0.4.rem, 0.9.rem),
        fontWeight(600),
        cursor.pointer,
        Selector(PseudoClass.hover, borderColor(terracotta), color(terracotta)),
      )

  object Maker
      extends CssClass(
        display.grid,
        gridTemplateColumns("5.5rem 1fr"),
        gap(1.4.rem),
        alignItems.start,
        MediaQuery(Media.maxWidth.px(640), gridTemplateColumns("1fr"), textAlign.center),
      )

  object MakerMark
      extends CssClass(
        width.px(88),
        height.auto,
        borderRadius.px(18),
        MediaQuery(Media.maxWidth.px(640), margin(0.px, Length.auto)),
      )

  object Bio
      extends CssClass(
        fontSize(1.05.rem),
        margin(0.px, 0.px, 1.1.rem, 0.px),
        maxWidth.px(40 * 16),
      )

  object Footer
      extends CssClass(
        padding(1.1.rem, 1.5.rem, 1.6.rem, 1.5.rem),
        borderTop(Border.solid(1.px, rule)),
        color(muted),
        fontSize(0.85.rem),
        textAlign.center,
        Selector(" a", color(muted)),
        Selector(" a:hover", color(chalk)),
        lightProse,
        MediaQuery(
          Media.prefersColorScheme.light,
          Selector(" a", color(lightMuted)),
          Selector(" a:hover", color(lightAccent)),
        ),
      )
end HubCss
