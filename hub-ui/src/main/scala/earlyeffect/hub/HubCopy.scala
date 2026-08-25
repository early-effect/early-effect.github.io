package earlyeffect.hub

/** Brand copy for the hub landing. */
object HubCopy:

  val title: String     = "Early Effect"
  val tagline: String   = "Scala 3 craft, from the first effect."
  val manifesto: String =
    "I write libraries that make the honest path the one that compiles. Docs that are programs. SQL the compiler can see. CI the build already knows. UI with the same ZIO you use everywhere else."

  val description: String =
    "Scala 3 and ZIO craft libraries: docs that are tests, SQL that cannot inject, CI the build describes, UI that is an effect."

  val origin: String = "https://www.earlyeffect.rocks"

  val makerName: String = "Russ White"
  val makerBio: String  =
    "I am Russ White. I break and make software and hardware, from Nashville. Early Effect is the public record of that: Scala 3 and ZIO tools I wanted in my own hands and did not want to keep private. If a type can carry the domain, it should. If a doc can lie, it should fail CI instead."

  val proofCaption: String =
    "This page is Ascent on ZIO. Library cards refresh from live metadata when JavaScript runs."

  val slogans: Vector[String] = Vector(
    "SQL that cannot inject",
    "Docs that fail CI",
    "CI the build describes",
    "UI that is an effect",
    "Types that carry the domain",
    "Machines you can name",
    "Diagrams styled with CSS",
    "Splice JS without Node",
  )

  val githubOrg: String    = "https://github.com/early-effect"
  val githubUser: String   = "https://github.com/russwyte"
  val mavenCentral: String = "https://central.sonatype.com/namespace/rocks.earlyeffect"
  val xProfile: String     = "https://x.com/russwyte"
  val heroImage: String    = "images/logo-hero.png"

  final case class Rule(n: String, title: String, body: String, ticks: Vector[String])

  val rules: Vector[Rule] = Vector(
    Rule(
      "01",
      "Docs that cannot rot",
      "A page is a program. Examples assert under zio-test. A red snippet fails CI instead of lying to the next reader.",
      Vector("specular", "marklit"),
    ),
    Rule(
      "02",
      "The domain lives in the types",
      "If the database cannot RETURNING, the call does not compile. States and events are enums. Transitions are effects.",
      Vector("saferis", "mechanoid"),
    ),
    Rule(
      "03",
      "The build is the source of truth",
      "YAML does not get a vote. The module graph describes CI. Jar names stay cache-stable between tags. JS splices without Node.",
      Vector("zipx", "sbt-dynver-ci", "sbt-splice"),
    ),
    Rule(
      "04",
      "UI is an effect",
      "The same ZIO you already trust, all the way to the DOM. No virtual DOM. Tests fire the control the UI shows.",
      Vector("ascent", "conduit", "chekhov"),
    ),
  )

  final case class Layer(id: String, title: String, thesis: String, names: Vector[String])

  val layers: Vector[Layer] = Vector(
    Layer(
      "write",
      "Write",
      "UI, state, SQL, and machines as typed ZIO.",
      Vector("ascent", "conduit", "saferis", "mechanoid"),
    ),
    Layer(
      "prove",
      "Prove",
      "Docs, tests, and diagrams that cannot drift.",
      Vector("specular", "chekhov", "mermoid", "marklit"),
    ),
    Layer(
      "ship",
      "Ship",
      "CI the build already knows. No Node tax for Scala.js.",
      Vector("zipx", "sbt-dynver-ci", "sbt-splice"),
    ),
  )

  val blurbs: Map[String, String] = Map(
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
    "Craft" -> "#craft",
    "Stack" -> "#stack",
    "Proof" -> "#proof",
    "Maker" -> "#maker",
  )
end HubCopy
