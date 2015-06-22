package pl.klaudiabis.product

import akka.actor.Actor.Receive
import akka.actor.Props
import akka.contrib.pattern.ShardRegion
import akka.persistence.PersistentView
import pl.klaudiabis.common.{AutoPassivation, ProductId}

object ProductView {

  val shardName = "product"

  val props = Props[ProductView]

  case class GetProductQuery(productId: ProductId)
  private case object GetProduct

  val idExtractor: ShardRegion.IdExtractor = {
    case GetProductQuery(productId) => (productId.toString, GetProduct)
  }

  val shardResolver: ShardRegion.ShardResolver = {
    case GetProductQuery(productId) => s"${productId.hashCode() % 10}"
  }

}

class ProductView extends PersistentView with AutoPassivation {

  private val productId = ProductId(self.path.name)

  override def persistenceId: String = s"product-${productId.toString}"

  override def viewId: String = s"product-${productId.toString}-view"

  override def receive: Receive = ???


}
