package pl.klaudiabis.product

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{StatusCodes, StatusCode}
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import pl.klaudiabis.common.{ProductId, Timeouts}
import pl.klaudiabis.product.Product.GetProductQuery
import pl.klaudiabis.product.ProductProcessor.{GetProductsQuery, AddProductCommand}

import scala.concurrent.ExecutionContextExecutor


trait ProductService extends SprayJsonSupport with ProductProtocols {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  import akka.pattern.ask
  import Timeouts.defaults._

  def productRoute(productProcessor: ActorRef, product: ActorRef) = {
    logRequest("product-microservice") {
      pathPrefix("product") {
        (post & entity(as[ProductSummary])) { productSummary =>
          productProcessor ? AddProductCommand(productSummary)
          complete {
            StatusCodes.OK
          }
        } ~
          (get & path(JavaUUID)) { productId =>
            complete {
              (product ? GetProductQuery(ProductId(productId.toString))).mapTo[ProductDetails]
            }
          } ~ get {
          complete {
            (productProcessor ? GetProductsQuery).mapTo[List[ProductSummary]]
          }
        }
      }
    }
  }

}
