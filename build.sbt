val scala3Version     = "3.8.4"
val zioVersion        = "2.1.26"
val zioHttpVersion    = "3.11.3"
val specularVersion   = "0.1.0"

ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "rocks.earlyeffect"

lazy val specularSite = taskKey[Unit]("Build the Early Effect hub site")

name           := "early-effect-hub"
publish / skip := true

libraryDependencies ++= Seq(
  "rocks.earlyeffect" %% "specular-site" % specularVersion,
  "dev.zio"           %% "zio"          % zioVersion,
  "dev.zio"           %% "zio-http"     % zioHttpVersion,
)

run / fork := true
run / javaOptions ++= Seq(
  "--sun-misc-unsafe-memory-access=allow",
  "--enable-native-access=ALL-UNNAMED",
)

Compile / mainClass := Some("earlyeffect.hub.BuildHub")

specularSite := Def.uncached {
  (Compile / runMain).toTask(" earlyeffect.hub.BuildHub").value
}
