name in ThisBuild := """quadmist"""
scalaVersion in ThisBuild := "2.13.2"
version in ThisBuild := "0.1.0-SNAPSHOT"
organization in ThisBuild := "com.lambdarat"

lazy val quadmist = (project in file("."))
  .dependsOn(`quadmist-common`, `quadmist-server`)
  .aggregate(`quadmist-common`, `quadmist-server`)

lazy val `quadmist-server` = project
  .settings(
    libraryDependencies ++= Seq(
      simulacrum,
      slf4jSimple,
      newtype,
      mouse,
      scalacheck % Test
    ) ++ monocle ++ circe ++ memeid ++ enumeratum ++ scalatest ++ cats ++ http4s,
    kindProjector,
    scalacOptions ++= Seq(
      "-Ymacro-annotations",
      "-language:higherKinds"
    )
  )
  .dependsOn(`quadmist-common`)

lazy val `quadmist-common` = project
  .settings(
    libraryDependencies ++= Seq(
      newtype,
      scalacheck % Test
    ) ++ monocle ++ circe ++ memeid ++ enumeratum ++ scalatest ++ cats,
    kindProjector,
    scalacOptions ++= Seq(
      "-Ymacro-annotations",
      "-language:higherKinds"
    )
  )
