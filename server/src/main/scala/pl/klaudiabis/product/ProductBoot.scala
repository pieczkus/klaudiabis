package pl.klaudiabis.product

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}

case class ProductBoot(productProcessor: ActorRef, product: ActorRef) {

}

object ProductBoot {

  def boot(implicit system: ActorSystem): ProductBoot = {
    val product = ClusterSharding(system).start(
            typeName = Product.shardName,
            entityProps = Product.props,
            settings = ClusterShardingSettings(system),
            extractEntityId = Product.idExtractor,
            extractShardId = Product.shardResolver)

    val productProcessor = system.actorOf(ProductProcessor.props(product))

    ProductBoot(productProcessor, product)
  }
}
