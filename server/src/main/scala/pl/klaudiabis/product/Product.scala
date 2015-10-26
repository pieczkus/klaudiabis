package pl.klaudiabis.product

import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import akka.cluster.sharding.ShardRegion
import akka.persistence.{PersistentActor, SnapshotOffer}
import pl.klaudiabis.common.{AutoPassivation, ProductId}
import pl.klaudiabis.metrics.ProductViewSummary.ViewProductCommand
import pl.klaudiabis.product.Product._

object Product {

  val shardName = "product"

  val props = Props[Product]

  sealed trait ProductCommand {
    def productId: ProductId
  }

  case class GetProduct(productId: ProductId) extends ProductCommand

  case class AddProduct(productId: ProductId, summary: ProductSummary) extends ProductCommand

  case class AddPicture(productId: ProductId, picture: String) extends ProductCommand

  case class GetProductPictures(productId: ProductId) extends ProductCommand

  sealed trait ProductEvent

  case class ProductAdded(summary: ProductSummary) extends ProductEvent

  case class PictureAdded(picture: String) extends ProductEvent

  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: ProductCommand => (cmd.productId.toString, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case cmd: ProductCommand => (math.abs(cmd.productId.hashCode) % 100).toString
  }

  private case class State(summary: ProductSummary, pictures: Pictures) {

    def updated(evt: ProductEvent): State = evt match {
      case ProductAdded(s) => copy(summary = s, pictures = pictures.withNewPicture(s.thumbnailUrl))
      case PictureAdded(p) => copy(pictures = pictures.withNewPicture(p))
    }
  }

}

class Product extends PersistentActor with AutoPassivation {

  override def persistenceId: String = "product-" + self.path.parent.name + "-" + self.path.name

  private var product = State(null, Pictures.empty)

  private def notExists: Receive = withPassivation {
    case AddProduct(_, summary) =>
      log.info(s"what? product to persist? wow $summary")
      persist(ProductAdded(summary)) { pa =>
        product = product.updated(pa)
        saveSnapshot(product)
        context.become(exists)
      }
  }

  private def exists: Receive = withPassivation {
    case GetProduct(pId) =>
      sender() ! product.summary

    case AddPicture(_, picture) =>
      persist(PictureAdded(picture)) { pa =>
        product = product.updated(pa)
        saveSnapshot(product)
      }
    case GetProductPictures(_) => sender() ! product.pictures.urls()
  }

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, snapshot: State) =>
      log.info("SnapshotOffer: not exist -> exists.")
      product = snapshot
      context.become(exists)
    case evt: ProductEvent =>
      log.info("Recover product huhuhuhuhu")
      this.product = product.updated(evt)
  }

  override def receiveCommand: Receive = notExists
}
