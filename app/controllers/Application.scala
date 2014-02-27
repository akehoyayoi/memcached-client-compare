package controllers

import play.api._
import play.api.mvc._
import net.rubyeye.xmemcached.MemcachedClient
import com.dongxiguo.memcontinuationed.{Memcontinuationed,StorageAccessor}
import java.io._
import java.net._
import scala.util.continuations.reset
import scala.util.control.Exception.Catcher
import java.nio.channels.AsynchronousChannelGroup
import java.util.concurrent.Executors
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.util.Timeout

import com.klout.akkamemcached.RealMemcachedClient
import com.klout.akkamemcached.Serialization.JBoss


case class MyKey(override val key: String) extends StorageAccessor[String] {
  override def encode(output: OutputStream, data: String, flags: Int) {
    output.write(data.getBytes("UTF-8"))
  }
  override def decode(input: InputStream, flags: Int): String = {
    val result = new Array[Byte](input.available)
    input.read(result)
    new String(result, "UTF-8")
  }
}

object CompareMemcachedClient {
  var xmemcachedClient : MemcachedClient = null
  var memcontinuationed : Memcontinuationed = null
}

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val rand = new scala.util.Random()
  def xmemcached(totalRequests : Int , bodyLength : Int) = Action {
    val memcached = CompareMemcachedClient.xmemcachedClient
    val s = (1 to totalRequests).flatMap { _ =>
      val k = s"kkkkkkkkkkkkkkkk${rand.nextInt(32)}"
      val v = "v" * 1024 * bodyLength
      memcached.set(k, 1, v)
      val res: Option[String] = Option(memcached.get[String](k))
      res.getOrElse("null")
    }.mkString
    Ok(s"length=${s.length}\n")
  }

  implicit def catcher:Catcher[Unit] = {
    case e: Exception =>
      scala.Console.err.print(e)
  }

  lazy val memcontinuationedClient = {
    val threadPool = Executors.newCachedThreadPool()
    val channelGroup = AsynchronousChannelGroup.withThreadPool(threadPool)

    // The locator determines where the memcached server is.
    // You may want to implement ketama hashing here.
    def locator(accessor: StorageAccessor[_]) = {
      new InetSocketAddress("localhost", 11211)
    }
    new Memcontinuationed(channelGroup, locator)
  }

  def memcontinuationed(totalRequests : Int , bodyLength : Int) = Action {
    val memcached = memcontinuationedClient
    val s = ""
//    val s = (1 to totalRequests).flatMap { _ =>
//      val k = MyKey(s"kkkkkkkkkkkkkkkk${rand.nextInt(32)}")
//      val v = "v" * 1024 * bodyLength
//      memcached.set(k,v)
//      val res: Option[String] = Option(memcached.require(k))
//      res.getOrElse("null")
//    }.mkString
    Ok(s"length=${s.length}\n")
  }

  import scala.concurrent.Future

  lazy val akkaClient = new RealMemcachedClient(List(("localhost", 11211)), 1)
  def akka(totalRequests : Int , bodyLength : Int) = Action {
    val memcached = akkaClient
    val s = (1 to totalRequests).flatMap { _ =>
      val k = s"kkkkkkkkkkkkkkkk${rand.nextInt(32)}"
      val v = "v" * 1024 * bodyLength
      memcached.set(k, v , 1 hour)
      val valueFuture = memcached.get(k)
      val res: Option[String] = Await.result(valueFuture, 5 seconds)
      res.getOrElse("null")
    }.mkString
    Ok(s"length=${s.length}\n")
  }

}