package pl.klaudiabis.product

import akka.actor.{ActorRef, ActorSystem}
import akka.contrib.pattern.ClusterSharding

case class ProductBoot(productProcessor: ActorRef, product: ActorRef) {

}

object ProductBoot {

  def boot(implicit system: ActorSystem): ProductBoot = {
    val productView = ClusterSharding(system).start(
      typeName = Product.shardName,
      entryProps = Some(Product.props),
      idExtractor = Product.idExtractor,
      shardResolver = Product.shardResolver)

    val productProcessor = system.actorOf(ProductProcessor.props(productView))

    ProductBoot(productProcessor, productView)
  }
}
