package pl.klaudiabis.product

import akka.actor.{ActorSystem, ActorRef}
import akka.contrib.pattern.ClusterSharding
import pl.klaudiabis.common.MicroServiceApp.BootedNode

import scala.concurrent.ExecutionContext

case class ProductBoot(productProcessor: ActorRef, productView: ActorRef) extends ProductService with BootedNode {

  def route(ec: ExecutionContext) = productRoute(productProcessor, productView)(ec)

  override def api = Some(route)

}

object ProductBoot {

  def boot(implicit system: ActorSystem): ProductBoot = {
    val productView = ClusterSharding(system).start(
      typeName = ProductView.shardName,
      entryProps = Some(ProductView.props),
      idExtractor = ProductView.idExtractor,
      shardResolver = ProductView.shardResolver)

    val productProcessor = system.actorOf(ProductProcessor.props(productView))

    ProductBoot(productProcessor, productView)
  }
}
