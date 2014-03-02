import sbt._

object Dependencies {
  val serverDependencies = Seq(
    // Add your project dependencies here,
    "com.googlecode.xmemcached" % "xmemcached" % "1.4.1",
    "com.klout" %% "akka-memcached" % "0.91"
  )
}

