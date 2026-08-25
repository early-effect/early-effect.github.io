import zipx.*

/** Typed catalog: every library and plugin this build may use. `zipxDepUpdate` rewrites constructors here.
  *
  * sbt-zipx is not a row: generate emits it from the loaded plugin (`zipxSelfPlugins`). sbt-pgp is not a row: zipx
  * already brings it in. Action pins stay on jar defaults.
  *
  * Parent `Lib` vals used only for `.mod` are catalog rows. Hub JS selects `specular-core` directly; the docs theme
  * already pulls `specular-site`.
  */
object MyVersions extends ZipxVersions:
  val sbt: SbtVersion     = SbtVersion("2.0.7")
  val scala: ScalaVersion = ScalaVersion("3.8.4")

  val zio     = Lib("dev.zio", "zio", "2.1.26")
  val zioHttp = Lib("dev.zio", "zio-http", "3.11.3")

  val ascentJs = Lib("rocks.earlyeffect", "ascent-js", "0.4.1")

  val specular      = Lib("rocks.earlyeffect", "specular-core", "0.14.0")
  val specularSite  = specular.mod("specular-site")
  val specularTheme = specular.mod("early-effect-docs-theme")

  val scalaJavaTime     = Lib("io.github.cquiroz", "scala-java-time", "2.7.0")
  val scalaJavaTimeTzdb = scalaJavaTime.mod("scala-java-time-tzdb")

  val scalajs  = Plugin("org.scala-js", "sbt-scalajs", "1.22.0")
  val scalafmt = Plugin("org.scalameta", "sbt-scalafmt", "2.6.2")
  val dynverCi = Plugin("rocks.earlyeffect", "sbt-dynver-ci", "0.2.2")
  val splice   = Plugin("rocks.earlyeffect", "sbt-splice", "0.1.0")

  def hubJs = library(specular, ascentJs, zio, scalaJavaTime, scalaJavaTimeTzdb)
  def hub   = library(specularSite, specularTheme, zio, zioHttp)
end MyVersions
