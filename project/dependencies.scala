import sbt._

object Dependencies {
  val serverDependencies = Seq(
    // Add your project dependencies here,  
    "com.googlecode.xmemcached" % "xmemcached" % "1.4.1",
    "com.dongxiguo" % "memcontinuationed_2.10" % "0.3.1"
  )
}

