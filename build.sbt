name in ThisBuild := """quadmist"""
scalaVersion in ThisBuild := "2.13.2"
version in ThisBuild := "0.1.0-SNAPSHOT"
organization in ThisBuild := "com.lambdarat"

lazy val quadmist = (project in file("."))
  .dependsOn(`quadmist-server`)
  .aggregate(`quadmist-common`, `quadmist-server`)

lazy val `quadmist-common` = ProjectRef(file("quadmist-common"), "quadmist-common")

lazy val `quadmist-server` = project
  .settings(
    libraryDependencies ++= serverDependencies,
    scalacOptions ++= Seq(
      "-Ymacro-annotations",
      "-language:higherKinds"
    )
  )
  .dependsOn(`quadmist-common`)
