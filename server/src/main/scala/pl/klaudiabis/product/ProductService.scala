package pl.klaudiabis.product

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext

trait ProductService {


  def productRoute(productProcessor: ActorRef, productView: ActorRef)(implicit ec: ExecutionContext) = {
    logRequest("product-microservice") {
      pathPrefix("product") {
        get {
          complete("OK")
        }
      }
    }
  }

}
