name in ThisBuild := """quadmist"""
scalaVersion in ThisBuild := "2.13.2"
version in ThisBuild := "0.1.0-SNAPSHOT"
organization in ThisBuild := "com.lambdarat"

lazy val quadmist = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      memeid,
      newtype,
      enumeratum,
      mouse,
      scalacheck % Test
    ) ++ scalatest ++ cats,
    kindProjector
  )

scalacOptions += "-Ymacro-annotations"
