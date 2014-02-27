logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.dongxiguo" % "memcontinuationed_2.10" % "0.3.1"

libraryDependencies <+= scalaVersion { v =>
  compilerPlugin("org.scala-lang.plugins" % "continuations" % v)
}

scalacOptions += "-P:continuations:enable"

scalaVersion := "2.10.0"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")