package pl.klaudiabis.metrics

import java.time.LocalDate

import akka.actor.Props
import akka.cluster.sharding.ShardRegion
import akka.persistence.{SnapshotOffer, PersistentActor}
import pl.klaudiabis.common.{MetricId, AutoPassivation, ProductId}
import pl.klaudiabis.metrics.ProductViewMetric._

object ProductViewMetric {

  val shardName = "metric"

  val props = Props[ProductViewMetric]

  sealed trait ProductMetricCommand {
    def productId: ProductId
  }

  case class GetAllViews(productId: ProductId) extends ProductMetricCommand

  case class RecordView(productId: ProductId) extends ProductMetricCommand

  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: ProductMetricCommand => (cmd.productId.metricId, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case cmd: ProductMetricCommand => (math.abs(cmd.productId.metricId.hashCode) % 100).toString
  }

  sealed trait ProductMetricEvent

  case object ProductViewed extends ProductMetricEvent

  private case class MetricState(views: ProductViews) {

    def updated(evt: ProductMetricEvent): MetricState = evt match {
      case ProductViewed => copy(views = views.view(LocalDate.now()))
    }
  }

}

class ProductViewMetric extends PersistentActor with AutoPassivation {

  override def persistenceId: String = "metric-" + self.path.parent.name + "-" + self.path.name

  private var state = MetricState(ProductViews.empty)

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, snapshot: MetricState) =>
      state = snapshot
    case evt: ProductMetricEvent =>
      this.state = state.updated(evt)
  }

  override def receiveCommand: Receive = {
    case GetAllViews(_) =>
      sender() ! state.views.viewsByDay

    case RecordView(_) =>
      persist(ProductViewed) { evt =>
        state = state.updated(evt)
        saveSnapshot(state)
      }
  }


}
