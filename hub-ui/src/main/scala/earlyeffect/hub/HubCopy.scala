package earlyeffect.hub

/** Brand copy for the hub landing. */
object HubCopy:

  val title: String   = "Early Effect"
  val tagline: String = "Scala 3 craft, from the first effect."
  val markAlt: String = "A ceramic bird and circuit mark"

  val description: String =
    "Scala 3 and ZIO craft libraries: docs that are tests, SQL that cannot inject, HTTP as an effect, CI the build describes, UI that is an effect."

  val origin: String = "https://www.earlyeffect.rocks"

  val makerName: String = "Russ White"
  val makerLine: String = "I break and make software and hardware, from Nashville."

  val githubOrg: String    = "https://github.com/early-effect"
  val githubUser: String   = "https://github.com/russwyte"
  val mavenCentral: String = "https://central.sonatype.com/namespace/rocks.earlyeffect"
  val xProfile: String     = "https://x.com/russwyte"
  val heroImage: String    = "images/logo-hero.png"

  def sameOrigin(href: String): Boolean =
    href.startsWith("/") || href.startsWith("#") || href == origin || href.startsWith(s"$origin/")

  final case class Layer(id: String, title: String, thesis: String, names: Vector[String])

  val layers: Vector[Layer] = Vector(
    Layer(
      "write",
      "Write",
      "The domain lives in the types, and UI is the same ZIO all the way to the DOM.",
      Vector("heddle", "ascent", "conduit", "saferis", "mechanoid"),
    ),
    Layer(
      "prove",
      "Prove",
      "A page is a program, and a red snippet fails CI instead of lying to the next reader.",
      Vector("specular", "chekhov", "mermoid", "marklit"),
    ),
    Layer(
      "ship",
      "Ship",
      "The build is the source of truth: YAML does not get a vote.",
      Vector("zipx", "sbt-dynver-ci", "sbt-splice"),
    ),
  )

  val blurbs: Map[String, String] = Map(
    "heddle"  -> "HTTP as an effect. One BoundOp for humans, systems, and agents: HTTP, OpenAPI, MCP, stdio.",
    "ascent"  -> "Effect-native reactive UI for Scala 3. Renders straight to the DOM: no virtual DOM, no diffing.",
    "conduit" -> "Unidirectional state on ZIO. Actions, handlers, lenses. JVM, Scala.js, and Native.",
    "saferis" -> "Type-safe SQL for Scala 3 and ZIO. Injection is a compile error. Dialects gate what exists.",
    "mechanoid" -> "Typed finite state machines on ZIO. States and events as enums. Assemblies checked at compile time.",
    "specular"      -> "Tests-as-docs. A DocSpec asserts in CI and SSR-renders through Ascent.",
    "chekhov"       -> "ZIO-first Playwright. If the UI shows a control, a test should be able to fire it.",
    "mermoid"       -> "Mermaid to SVG in Scala 3, styled with real CSS. No Chrome, no Node at page load.",
    "marklit"       -> "Markdown whose Scala fences compile and run, even across 2.13 and 3.x in one file.",
    "zipx"          -> "sbt 2 plugin: the build describes its own GitHub Actions CI.",
    "sbt-zipx"      -> "sbt 2 plugin: the build describes its own GitHub Actions CI.",
    "sbt-dynver-ci" -> "Cache-friendly dynver: stable jar names between tags so CI caches actually hit.",
    "sbt-splice"    -> "Splice pinned JS into Scala.js linker output. No npm. No Node.",
  )

  final case class ProofBeat(label: String, compiles: String, fails: String, note: String)

  val proofBeats: Vector[ProofBeat] = Vector(
    ProofBeat(
      "heddle",
      """Endpoint.get("users" / int("id")).out[User].mcp""",
      "a second tool DSL beside the REST routes",
      "BoundOp is the capability. HTTP, OpenAPI, and MCP are hosts, not copies.",
    ),
    ProofBeat(
      "saferis",
      """sql"select * from users where id = $id"""",
      """s"select * from users where id = $id"""",
      "The interpolator will not let you splice a value into SQL. Concatenation will.",
    ),
    ProofBeat(
      "zipx",
      "zipxTestTask := zipxTasks.of(testFull)",
      "jobs:\n  test:\n    runs-on: ubuntu-latest\n    # hope this still matches build.sbt",
      "The module graph is the workflow. A second copy in YAML is how CI rots.",
    ),
    ProofBeat(
      "ascent",
      """E.span(count.map(_.toString))""",
      "re-render the whole tree and hope the input keeps focus",
      "Only the text node behind the Squawk patches. The rest of the tree stays put.",
    ),
  )

  val nav: Vector[(String, String)] = Vector(
    "Write" -> "#write",
    "Prove" -> "#prove",
    "Ship"  -> "#ship",
    "Proof" -> "#proof",
  )
end HubCopy
