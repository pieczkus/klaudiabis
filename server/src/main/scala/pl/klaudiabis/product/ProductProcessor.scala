package pl.klaudiabis.product

import akka.actor.{Props, ActorRef, ActorLogging}
import akka.persistence.PersistentActor
import pl.klaudiabis.common.AutoPassivation

object ProductProcessor {

  def props(productView: ActorRef) = Props(classOf[ProductProcessor], productView)

  val shardName = "product"


}

class ProductProcessor(productView: ActorRef) extends PersistentActor with AutoPassivation {

  override def persistenceId: String = "product-processor"

  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = ???
}
