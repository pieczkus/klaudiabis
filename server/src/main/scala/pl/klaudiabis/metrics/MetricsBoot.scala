package pl.klaudiabis.metrics

import akka.actor.{ActorSystem, ActorRef}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}

case class MetricsBoot(metricsSummary: ActorRef, productMetric: ActorRef) {

}

object MetricsBoot {

  def boot(implicit system: ActorSystem): MetricsBoot = {
    val productMetric = ClusterSharding(system).start(
      typeName = ProductViewMetric.shardName,
      entityProps = ProductViewMetric.props,
      settings = ClusterShardingSettings(system),
      extractEntityId = ProductViewMetric.idExtractor,
      extractShardId = ProductViewMetric.shardResolver)

    val metricsSummary = system.actorOf(ProductViewSummary.props(productMetric))

    MetricsBoot(metricsSummary, productMetric)
  }
}
