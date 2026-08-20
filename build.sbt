MyVersions.settings

organization := "rocks.earlyeffect"
// Version from sbt-dynver-ci (cache-stable `-ci` between tags).

// zipx: verify hub build. Pages deploy stays in hub-site.yml.
zipxJavaVersion := JdkVersion("25")
// Builtin Verify is parallel: fmt, workflow-check, advisories, test (zipxTestTask).
zipxTestTask := zipxTasks.of(specularSite)

lazy val specularSite =
  taskKey[Unit]("Link hub JS + build the Early Effect hub site")

lazy val hubJS = project
  .in(file("hub-js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(MyVersions.hubJs)
  .settings(
    name := "early-effect-hub-js",
    publish / skip := true,
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.ESModule)),
    Compile / mainClass := Some("earlyeffect.hub.ClientMain")
  )

lazy val root = project
  .in(file("."))
  .settings(MyVersions.hub)
  .settings(
    name := "early-effect-hub",
    publish / skip := true,
    run / fork := true,
    run / javaOptions ++= Seq(
      "--sun-misc-unsafe-memory-access=allow",
      "--enable-native-access=ALL-UNNAMED"
    ),
    Compile / mainClass := Some("earlyeffect.hub.BuildHub"),
    // Write the client.js marker, then Fork.java BuildHub. Do not use
    // `(Compile / runMain).toTask(...).value` in this same block: sbt hoists all
    // `.value` calls, so runMain would run before IO.write and CI would fail on a
    // clean checkout ("JS client not linked").
    specularSite := Def.uncached {
      val log       = streams.value.log
      val converter = fileConverter.value
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

      (Compile / compile).value
      val jars =
        (Compile / fullClasspath).value
          .map(af => converter.toPath(af.data).toFile.getAbsolutePath)
      val jvmOpts   = (run / javaOptions).value.toVector
      val mainClass = "earlyeffect.hub.BuildHub"
      log.info(s"specularSite: running $mainClass")
      val code = Fork.java(
        ForkOptions()
          .withOutputStrategy(Some(LoggedOutput(log)))
          .withRunJVMOptions(jvmOpts),
        Seq("-cp", jars.mkString(java.io.File.pathSeparator), mainClass),
      )
      if code != 0 then sys.error(s"$mainClass failed with exit code $code")
    },
  )
