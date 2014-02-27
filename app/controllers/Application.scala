package controllers

import play.api._
import play.api.mvc._
import java.io._
import com.dongxiguo.memcontinuationed.{Memcontinuationed, StorageAccessor}
import net.rubyeye.xmemcached.MemcachedClient
import scala.util.control.Exception.Catcher

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

  def memcontinuationed(totalRequests : Int , bodyLength : Int) = Action {
    val memcached = CompareMemcachedClient.memcontinuationed
    val s = (1 to totalRequests).flatMap { _ =>
      val k = MyKey(s"kkkkkkkkkkkkkkkk${rand.nextInt(32)}")
      val v = "v" * 1024 * bodyLength
      // [[RuntimeException: java.lang.NoSuchMethodError: com.dongxiguo.memcontinuationed.Memcontinuationed.set(Lcom/dongxiguo/memcontinuationed/StorageAccessor;Ljava/lang/Object;JLscala/PartialFunction;)V]]
      memcached.set(k,v)
      val res: Option[String] = Option(memcached.require(k))
      res.getOrElse("null")
    }.mkString
    Ok(s"length=${s.length}\n")
  }

}