import sbt._
import sbt.plugins.JvmPlugin

object ProjectPlugin extends AutoPlugin {

  object autoImport {
    lazy val scalatest  = Seq(
      "org.scalatest"     %% "scalatest"       % "3.1.2",
      "org.scalatestplus" %% "scalacheck-1-14" % "3.1.2.0"
    ).map(_ % Test)
    lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.1"

    private lazy val catsV       = "2.1.1"
    lazy val cats                = Seq(
      "org.typelevel" %% "cats-core",
      "org.typelevel" %% "cats-effect"
    ).map(_ % catsV)
    lazy val mouse               = "org.typelevel" %% "mouse"   % "0.25"
    lazy val kindProjector       = addCompilerPlugin(
      "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
    )
    private lazy val enumeratumV = "1.6.1"
    lazy val enumeratum          = Seq(
      "com.beachape" %% "enumeratum",
      "com.beachape" %% "enumeratum-circe"
    ).map(_ % enumeratumV)

    lazy val newtype             = "io.estatico"   %% "newtype" % "0.4.4"

    private lazy val memeidV = "0.1"
    lazy val memeid          = Seq(
      "com.47deg" %% "memeid4s",
      "com.47deg" %% "memeid4s-http4s",
      "com.47deg" %% "memeid4s-cats",
      "com.47deg" %% "memeid4s-circe",
      "com.47deg" %% "memeid4s-scalacheck",
      "com.47deg" %% "memeid4s-literal"
    ).map(_ % memeidV)

    lazy val http4sV         = "0.21.4"
    lazy val http4s          = Seq(
      "org.http4s" %% "http4s-blaze-server" % http4sV,
      "org.http4s" %% "http4s-circe"        % http4sV,
      "org.http4s" %% "http4s-dsl"          % http4sV
    )

    private lazy val circeVersion = "0.13.0"
    lazy val circe                = Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-generic-extras",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)

    lazy val slf4jSimple          = "org.slf4j" % "slf4j-simple" % "1.7.30"

    lazy val simulacrum = "org.typelevel" %% "simulacrum" % "1.0.0"

    private lazy val monocleV = "2.0.0"
    lazy val monocle          = Seq(
      "com.github.julien-truffaut" %% "monocle-core",
      "com.github.julien-truffaut" %% "monocle-macro"
    ).map(_ % monocleV)

    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  }

  override def requires: Plugins = JvmPlugin

  override def trigger: PluginTrigger = allRequirements
}
