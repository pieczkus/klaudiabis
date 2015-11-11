package pl.klaudiabis.contact

import akka.actor.Actor

class ContactWorkExecutor extends Actor {

  override def receive: Receive = {
    case n: Int =>
      val n2 = n * n
      val result = s"$n * $n = $n2"
      sender() ! ContactWorker.WorkComplete(result)
  }
}
