name in ThisBuild := """quadmist"""
scalaVersion in ThisBuild := "2.13.2"
version in ThisBuild := "0.1.0-SNAPSHOT"
organization in ThisBuild := "com.lambdarat"

lazy val quadmist = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      simulacrum,
      slf4jSimple,
      newtype,
      mouse,
      scalacheck % Test
    ) ++ circe ++ memeid ++ enumeratum ++ scalatest ++ cats ++ http4s,
    kindProjector
  )

scalacOptions ++= Seq(
  "-Ymacro-annotations",
  "-language:higherKinds"
)
