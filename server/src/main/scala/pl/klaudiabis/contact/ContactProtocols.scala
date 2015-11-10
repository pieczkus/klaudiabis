package pl.klaudiabis.contact

import akka.actor.ActorSystem
import akka.stream.Materializer
import pl.klaudiabis.common.CommonProtocols
import pl.klaudiabis.metrics.Metric

import scala.concurrent.ExecutionContextExecutor

trait ContactProtocols extends CommonProtocols {

  implicit val contactMessageFormat = jsonFormat4(ContactMessage.apply)

}
