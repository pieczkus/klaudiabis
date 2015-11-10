package pl.klaudiabis.contact

import akka.actor.{ActorSystem, ActorRef}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer

import scala.concurrent.ExecutionContextExecutor

trait ContactService extends SprayJsonSupport with ContactProtocols {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  import pl.klaudiabis.common.Timeouts.defaults._

  def contactRoute(metricsSummary: ActorRef): Unit = {
    logRequest("contact-microservice") {
      pathPrefix("contact") {
        (post & entity(as[ContactMessage])) { contactMessage =>



          complete {
            StatusCodes.OK
          }
        }
      }
    }
  }

}
