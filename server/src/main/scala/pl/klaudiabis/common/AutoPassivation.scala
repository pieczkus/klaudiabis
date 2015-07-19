package pl.klaudiabis.common

import akka.actor.{Actor, ActorLogging, ReceiveTimeout}

trait AutoPassivation extends ActorLogging {
  this: Actor =>

  import akka.contrib.pattern.ShardRegion.Passivate

  private val passivationReceive: Receive = {
    case ReceiveTimeout ⇒
      log.info("ReceiveTimeout: passivating.")
      context.parent ! Passivate(stopMessage = 'stop)
    case 'stop ⇒
      log.debug("'stop: bye-bye, cruel world, see you after recovery.")
      context.stop(self)
  }

  protected def withPassivation(receive: Receive): Receive = receive.orElse(passivationReceive)
}
