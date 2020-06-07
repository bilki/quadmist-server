import sbt._
import sbt.plugins.JvmPlugin

object ProjectPlugin extends AutoPlugin {

  object autoImport {
    lazy val scalatest = Seq(
      "org.scalatest"     %% "scalatest"       % "3.1.2",
      "org.scalatestplus" %% "scalacheck-1-14" % "3.1.2.0"
    ).map(_ % Test)
    lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.1"

    private lazy val catsV = "2.1.1"
    lazy val cats = Seq(
      "org.typelevel" %% "cats-core",
      "org.typelevel" %% "cats-effect"
    ).map(_ % catsV)
    lazy val mouse = "org.typelevel" %% "mouse" % "0.25"
    lazy val kindProjector = addCompilerPlugin(
      "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
    )
    lazy val enumeratum = "com.beachape" %% "enumeratum" % "1.6.1"
    lazy val newtype    = "io.estatico"  %% "newtype"    % "0.4.4"
    lazy val memeid     = "com.47deg"    %% "memeid4s"   % "0.1"
  }

  override def requires: Plugins = JvmPlugin

  override def trigger: PluginTrigger = allRequirements

}
