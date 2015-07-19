package pl.klaudiabis.product

import akka.actor.{ActorRef, Props}
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator.{Publish, Subscribe}
import akka.persistence.{PersistentActor, SnapshotOffer}
import pl.klaudiabis.common.{AutoPassivation, ProductId}
import pl.klaudiabis.product.ProductProcessor._

import scalaz.\/

object ProductProcessor {

  def props(productView: ActorRef) = Props(classOf[ProductProcessor], productView)

  val shardName = "product"

  case class AddProductCommand(product: ProductSummary)

  case class ProductAddedEvent(productId: ProductId, product: ProductSummary)

  case class NewProductAdded(product: ProductSummary)

  case object GetProductsQuery

  case object GetYearsQuery


}

class ProductProcessor(product: ActorRef) extends PersistentActor with AutoPassivation {

  override def persistenceId: String = "product-processor"

  private var products = Products.empty
  private val mediator = DistributedPubSubExtension(context.system).mediator
  private val topic = "products"
  mediator ! Subscribe(topic, self)

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, snapshot: Products) =>
      products = products.recover(snapshot.products)
      log.info(s"Got recover with products ${products.products}")
    case pa@ProductAddedEvent(productId, productSummary) =>
      log.info(s"Got recover with ${productSummary.name}")
      products = products.withNewProduct(productSummary)
      product ! pa
  }

  override def receiveCommand: Receive = withPassivation {
    case AddProductCommand(pc) =>
      if(productExists(pc.name)) {
        val message = s"Product with ${pc.name} already exists"
        log.warning(message)
        sender() ! \/.left(message)
      } else {
        val productId = ProductId.randomId()
        val productSummary = pc.withId(productId)
        persist(ProductAddedEvent(productId, productSummary)) { productAdded =>
          product ! productAdded
          products = products.withNewProduct(productSummary)
          saveSnapshot(products)

          mediator ! Publish(topic, NewProductAdded(productSummary))
        }
      }

    case NewProductAdded(productSummary) =>
      if(sender() != self) {
        log.debug(s"NewProductAdded => $productSummary")
        products = products.withNewProduct(productSummary)
      }

    case GetProductsQuery =>
      sender() ! products.products

    case GetYearsQuery =>
      sender() ! products.years()
  }

  def productExists(name: String): Boolean = products.get(name).isDefined
}
