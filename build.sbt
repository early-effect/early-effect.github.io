val scala3Version = "3.8.4"
val zioVersion = "2.1.26"
val zioHttpVersion = "3.11.3"
val ascentVersion = "0.1.0"
val specularVersion = "0.5.0"

ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "rocks.earlyeffect"
// Version from sbt-dynver-ci (cache-stable `-ci` between tags).

lazy val specularSite =
  taskKey[Unit]("Link hub JS + build the Early Effect hub site")

lazy val hubJS = project
  .in(file("hub-js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "early-effect-hub-js",
    publish / skip := true,
    libraryDependencies ++= Seq(
      "rocks.earlyeffect" %% "specular-core" % specularVersion,
      "rocks.earlyeffect" %% "ascent-js" % ascentVersion,
      "dev.zio" %% "zio" % zioVersion,
      "io.github.cquiroz" %% "scala-java-time" % "2.7.0",
      "io.github.cquiroz" %% "scala-java-time-tzdb" % "2.7.0"
    ),
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.ESModule)),
    Compile / mainClass := Some("earlyeffect.hub.ClientMain")
  )

lazy val root = project
  .in(file("."))
  .settings(
    name := "early-effect-hub",
    publish / skip := true,
    libraryDependencies ++= Seq(
      "rocks.earlyeffect" %% "specular-site" % specularVersion,
      "rocks.earlyeffect" %% "early-effect-docs-theme" % specularVersion,
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-http" % zioHttpVersion
    ),
    run / fork := true,
    run / javaOptions ++= Seq(
      "--sun-misc-unsafe-memory-access=allow",
      "--enable-native-access=ALL-UNNAMED"
    ),
    Compile / mainClass := Some("earlyeffect.hub.BuildHub"),
    specularSite := Def.uncached {
      val log = streams.value.log
      (hubJS / Compile / fastLinkJS).value
      val outDir = (hubJS / Compile / fastLinkJSOutput).value
      val mainJs = outDir / "main.js"
      if (!mainJs.exists)
        sys.error(
          s"Expected $mainJs after fastLinkJS; directory contains: " +
            Option(outDir.list).toSeq.flatten.mkString(", ")
        )
      val marker =
        (ThisBuild / baseDirectory).value / "target" / "hub-client-js.path"
      IO.write(marker, mainJs.getAbsolutePath)
      log.info(s"hubJS linked → $mainJs")
      (Compile / runMain).toTask(" earlyeffect.hub.BuildHub").value
    }
  )
