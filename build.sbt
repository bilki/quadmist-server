lazy val commonSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  organization := "com.lambdarat",
  scalaVersion := "2.13.3",
  scalacOptions ++= Seq(
    "-Ymacro-annotations",
    "-language:higherKinds"
  )
)

lazy val quadmist = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    mainClass in (Compile, run) := Some("com.lambdarat.quadmist.Quadmist"),
    test in `quadmist-common` := {} // Not needed here, run them in common repo
  )
  .dependsOn(`quadmist-server`)
  .aggregate(`quadmist-common`, `quadmist-server`)

lazy val `quadmist-common` = ProjectRef(file("quadmist-common"), "quadmist-common-subJVM")

lazy val `quadmist-server` = project
  .settings(commonSettings)
  .settings(libraryDependencies ++= serverDependencies)
  .dependsOn(`quadmist-common`)
