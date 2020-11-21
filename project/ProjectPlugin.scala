import sbt._
import sbt.plugins.JvmPlugin

object ProjectPlugin extends AutoPlugin {

  object autoImport {
    lazy val http4sV = "0.21.11"
    lazy val http4s  = Seq(
      "org.http4s" %% "http4s-blaze-server" % http4sV,
      "org.http4s" %% "http4s-circe"        % http4sV,
      "org.http4s" %% "http4s-dsl"          % http4sV
    )

    lazy val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.7.30"

    lazy val simulacrum = "org.typelevel" %% "simulacrum" % "1.0.0"

    private lazy val monocleV   = "2.0.3"
    lazy val monocle            = Seq(
      "com.github.julien-truffaut" %% "monocle-core",
      "com.github.julien-truffaut" %% "monocle-macro"
    ).map(_ % monocleV)

    lazy val serverDependencies = Seq(simulacrum, slf4jSimple) ++ monocle ++ http4s
  }

  override def requires: Plugins = JvmPlugin

  override def trigger: PluginTrigger = allRequirements
}
