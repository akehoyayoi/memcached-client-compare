
import com.dongxiguo.memcontinuationed.{StorageAccessor, Memcontinuationed}
import controllers.CompareMemcachedClient
import net.rubyeye.xmemcached.utils.AddrUtil
import net.rubyeye.xmemcached.{MemcachedClient, XMemcachedClientBuilder, XMemcachedClient}
import play.api._
import java.util.concurrent.Executors
import java.nio.channels.AsynchronousChannelGroup
import java.net._

/**
 * Created by okayayohei on 2014/02/27.
 */
object Global extends GlobalSettings {

  override def onStart(app: Application) {
    //  initialize XMemcached
    val builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("localhost:11211"))
    CompareMemcachedClient.xmemcachedClient = builder.build()

    // initialize Memcontinuationed
    val threadPool = Executors.newCachedThreadPool()
    val channelGroup = AsynchronousChannelGroup.withThreadPool(threadPool)
    // The locator determines where the memcached server is.
    // You may want to implement ketama hashing here.
    def locator(accessor: StorageAccessor[_]) = {
      new InetSocketAddress("localhost", 11211)
    }
    CompareMemcachedClient.memcontinuationed = new Memcontinuationed(channelGroup, locator)
  }

}
