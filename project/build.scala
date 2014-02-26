import sbt._
import Keys._
import play.Project._

object ServerBuild extends Build {
  val playSettings = play.Project.playScalaSettings
  val graphPluginSettings = net.virtualvoid.sbt.graph.Plugin.graphSettings
  val serverSettings = Defaults.defaultSettings ++ playSettings ++ graphPluginSettings ++ Seq(
    // Add your own project settings here
    scalaVersion := "2.10.2",
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-Yrecursion", "50"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    checksums in update := Nil,
    libraryDependencies ++= Dependencies.serverDependencies
  )
  val main = play.Project("compareMemcachedClient", settings=serverSettings)
}


// vim: set ts=4 sw=4 et:
