package pl.klaudiabis.metrics

import akka.actor.{Props, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, Publish}
import akka.persistence.{SnapshotOffer, PersistentActor}
import pl.klaudiabis.common.{MetricId, AutoPassivation, ProductId}
import pl.klaudiabis.metrics.ProductViewMetric.RecordView
import pl.klaudiabis.metrics.ProductViewSummary.{GetViewsSummary, NewProductView, ProductViewedEvent, ViewProductCommand}
import pl.klaudiabis.product.ProductProcessor.ProductAddedEvent

object ProductViewSummary {

  def props(productView: ActorRef) = Props(classOf[ProductViewSummary], productView)

  val shardName = "product-views-summary"

  case class ViewProductCommand(productId: ProductId)

  case class ProductViewedEvent(productId: ProductId)

  case class NewProductView(productId: ProductId)

  case object GetViewsSummary

}

class ProductViewSummary(productMetric: ActorRef) extends PersistentActor with AutoPassivation {

  override def persistenceId: String = "product-views-summary-processor"

  private val mediator = DistributedPubSub(context.system).mediator
  private val topic = "views-summary"
  mediator ! Subscribe(topic, self)

  private var summary: ViewsSummary = ViewsSummary.empty

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, snapshot: ViewsSummary) =>
      summary = snapshot
    case evt@NewProductView(productId) =>
      summary = summary.view(evt.productId)
  }

  override def receiveCommand: Receive = {
    case ViewProductCommand(pId) =>
      persist(ProductViewedEvent(pId)) { evt =>
        log.warning(s"incrementing summary IN ViewProductCommand from: ${summary.get(pId)}")
        summary = summary.view(evt.productId)
        productMetric ! RecordView(evt.productId)
        saveSnapshot(summary)
        mediator ! Publish(topic, NewProductView(evt.productId))
      }

    case NewProductView(productId) =>
      if (sender() != self) {
        log.warning(s"incrementing summary IN NewProductView from: ${summary.get(productId)}")
        summary = summary.view(productId)
      }

    case GetViewsSummary =>
      sender() ! summary.productViews
  }

}
