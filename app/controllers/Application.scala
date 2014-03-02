package controllers

import play.api._
import play.api.mvc._
import net.rubyeye.xmemcached.XMemcachedClientBuilder
import scala.concurrent.duration._
import scala.concurrent.{Future, Await}

import com.klout.akkamemcached.RealMemcachedClient
import com.klout.akkamemcached.Serialization.JBoss
import net.rubyeye.xmemcached.utils.AddrUtil

import play.api.libs.concurrent.Execution.Implicits._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  lazy val xmemcachedClient = {
    val builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("localhost:11211"))
    builder.build()
  }

  val rand = new scala.util.Random()
  def xmemcached(totalRequests : Int , bodyLength : Int) = Action {
    val memcached = xmemcachedClient
    val s = (1 to totalRequests).flatMap { _ =>
      val k = s"kkkkkkkkkkkkkkkk${rand.nextInt(32)}"
      val v = "v" * 1024 * bodyLength
      memcached.set(k, 1, v)
      val res: Option[String] = Option(memcached.get[String](k))
      res.getOrElse("null")
    }.mkString
    Ok(s"length=${s.length}\n")
  }

  lazy val akkaClient = new RealMemcachedClient(List(("localhost", 11211)), 1)
  def akkaComposite(totalRequests : Int , bodyLength : Int) = Action {
    val memcached = akkaClient
    val listOfFutures = (1 to totalRequests).toList.map { _ =>
      val k = s"kkkkkkkkkkkkkkkk${rand.nextInt(32)}"
      val v = "v" * 1024 * bodyLength
      memcached.set(k, v , 1 hour)
      memcached.get(k)
    }
    val futureList = Future.sequence(listOfFutures)
    val list = Await.result(futureList,5 seconds)
    val s = list.flatMap(_.getOrElse("null")).mkString
    Ok(s"length=${s.length}\n")
  }

  def akkaIndividual(totalRequests : Int , bodyLength : Int) = Action {
    val memcached = akkaClient
    val list = (1 to totalRequests).toList.map { _ =>
      val k = s"kkkkkkkkkkkkkkkk${rand.nextInt(32)}"
      val v = "v" * 1024 * bodyLength
      memcached.set(k, v , 1 hour)
      Await.result(memcached.get(k),5 seconds)
    }
    val s = list.flatMap(_.getOrElse("null")).mkString
    Ok(s"length=${s.length}\n")
  }

}