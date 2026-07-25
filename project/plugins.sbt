addSbtPlugin("org.scala-js"      % "sbt-scalajs"   % "1.22.0")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"  % "2.6.1")
addSbtPlugin("rocks.earlyeffect" % "sbt-dynver-ci" % "0.2.2")
addSbtPlugin("rocks.earlyeffect" % "sbt-zipx"      % "0.0.10")

// zipx bundles sbt-remote-cache; compiler-interface is on both sbt-2.x and zinc-1.x schemes.
libraryDependencySchemes += "org.scala-sbt" % "compiler-interface" % "always"
