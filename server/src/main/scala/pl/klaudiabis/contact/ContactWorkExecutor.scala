package pl.klaudiabis.contact

import akka.actor.Actor

class ContactWorkExecutor extends Actor {

  override def receive: Receive = {
    case msg: ContactMessage =>
      sender() ! ContactWorker.WorkComplete(s"Result ${msg.name} is a being contacted back")
  }
}
