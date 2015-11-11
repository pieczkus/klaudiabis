package pl.klaudiabis.contact

import java.util.UUID

import akka.actor.{ActorSystem, ActorRef}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.Materializer
import pl.klaudiabis.contact.ContactMaster.GetWorkers

import scala.concurrent.ExecutionContextExecutor

trait ContactService extends SprayJsonSupport with ContactProtocols {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  import pl.klaudiabis.common.Timeouts.defaults._
  import akka.pattern.ask

  def contactRoute(masterProxy: ActorRef): Route = {
    logRequest("contact-microservice") {
      pathPrefix("contact") {
        (post & entity(as[ContactMessage])) { contactMessage =>

          val work = Work(nextWorkId(), contactMessage)
          onSuccess(masterProxy ? work) {
            case ContactMaster.Ack(_) => complete {
              StatusCodes.Created
            }
            case _ => complete {
              StatusCodes.BadRequest
            }
          }
        } ~ (get & path("worker")) {
          complete {
            (masterProxy ? GetWorkers).mapTo[Set[String]]
          }
        }
      }
    }
  }

  def nextWorkId(): String = UUID.randomUUID().toString

}
