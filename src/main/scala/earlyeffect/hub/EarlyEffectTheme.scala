package earlyeffect.hub

import specular.site.{Theme, ThemeTokens}
import zio.ULayer

/** Hub-local brand theme.
  *
  * After the next Specular release that publishes `early-effect-docs-theme`, switch this
  * file to `earlyeffect.docs.EarlyEffectTheme` from that artifact and delete the tokens here.
  */
object EarlyEffectTheme:

  val tokens: ThemeTokens = ThemeTokens(
    bg = "#0d1117",
    surface = "#161b22",
    text = "#e6edf3",
    muted = "#9198a1",
    accent = "#7ee787",
    link = "#79c0ff",
    border = "#30363d",
    codeBg = "#010409",
    codeFg = "#e6edf3",
    fontSans = """-apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif""",
    radius = "12px",
    light = Some(
      ThemeTokens(
        bg = "#ffffff",
        surface = "#f6f8fa",
        text = "#1f2328",
        muted = "#59636e",
        accent = "#1a7f37",
        link = "#0969da",
        border = "#d0d7de",
        codeBg = "#161b22",
        codeFg = "#e6edf3",
        fontSans = """-apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif""",
        radius = "12px",
      )
    ),
  )

  val live: ULayer[Theme] = Theme.fromTokens(tokens)
end EarlyEffectTheme
