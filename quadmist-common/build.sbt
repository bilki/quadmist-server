name in ThisBuild := """quadmist-common"""
scalaVersion in ThisBuild := "2.13.2"
version in ThisBuild := "0.1.0-SNAPSHOT"
organization in ThisBuild := "com.lambdarat"

lazy val commonDependencies = Seq(newtype, scalacheck % Test) ++
  circe ++ memeid ++ enumeratum ++ cats ++ scalatest

addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full)
addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")

libraryDependencies ++= commonDependencies

scalacOptions ++= Seq(
  "-Ymacro-annotations",
  "-language:higherKinds"
)
