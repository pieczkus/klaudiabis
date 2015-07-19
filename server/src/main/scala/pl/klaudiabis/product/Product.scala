package pl.klaudiabis.product

import akka.actor.Props
import akka.contrib.pattern.ShardRegion
import akka.persistence.{PersistentActor, SnapshotOffer}
import pl.klaudiabis.common.{AutoPassivation, ProductId}
import pl.klaudiabis.product.Product.GetProduct
import pl.klaudiabis.product.ProductProcessor.ProductAddedEvent

object Product {

  val shardName = "product"

  val props = Props[Product]

  case class GetProductQuery(productId: ProductId)

  private case object GetProduct

  val idExtractor: ShardRegion.IdExtractor = {
    case GetProductQuery(productId) => (productId.toString, GetProduct)
    case ProductAddedEvent(productId, productSummary) => (productId.toString, productSummary)
  }

  val shardResolver: ShardRegion.ShardResolver = {
    case GetProductQuery(productId) => s"${productId.hashCode() % 10}"
    case ProductAddedEvent(productId, productSummary) => s"${productId.hashCode() % 10}"
  }

}

class Product extends PersistentActor with AutoPassivation {

  private val productId = ProductId(self.path.name)

  override def persistenceId: String = s"product-${productId.toString}"

  var product: ProductDetails = _

  private def notExists: Receive = withPassivation {
    case productSummary: ProductSummary =>
      log.info(s"what? product to persist? wow $productSummary")
      persist(productSummary) { ps =>
        product = new ProductDetails(ps, Pictures.empty)
        saveSnapshot(product)
        context.become(exists)
      }
  }

  private def exists: Receive = withPassivation {
    case GetProduct => sender() ! product
  }

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, snapshot: ProductDetails) =>
      log.info("SnapshotOffer: not exist -> exists.")
      product = snapshot
      context.become(exists)
    case productSummary: ProductSummary =>
      log.info("Recover product huhuhuhuhu")
      this.product = new ProductDetails(productSummary, Pictures.empty)
  }

  override def receiveCommand: Receive = notExists
}
