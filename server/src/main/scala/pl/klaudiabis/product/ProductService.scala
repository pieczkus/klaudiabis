package pl.klaudiabis.product

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import pl.klaudiabis.common.{ProductId, Timeouts}
import pl.klaudiabis.product.Product._
import pl.klaudiabis.product.ProductProcessor.{AddProductCommand, GetAllProducts}

import scala.concurrent.ExecutionContextExecutor


trait ProductService extends SprayJsonSupport with ProductProtocols {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  import akka.pattern.ask
  import pl.klaudiabis.common.Timeouts.defaults._

  def productRoute(productProcessor: ActorRef, product: ActorRef): Route = {
    logRequest("product-microservice") {
      pathPrefix("product") {
        (post & entity(as[ProductSummary])) { productSummary =>
          productProcessor ? AddProductCommand(productSummary)
          complete {
            StatusCodes.OK
          }
        } ~ (get & path(JavaUUID / "pictures")) { productId =>
          complete {
            (product ? GetProductPictures(ProductId(productId.toString))).mapTo[List[String]]
          }
        } ~ (get & path(JavaUUID)) { productId =>
          complete {
            (product ? GetProduct(ProductId(productId.toString))).mapTo[ProductSummary]
          }
        } ~ get {
          complete {
            (productProcessor ? GetAllProducts).mapTo[List[ProductSummary]]
          }
        } ~ (post & path(JavaUUID / "picture") & entity(as[List[String]])) { (productId, pictureUrls) =>
          pictureUrls.foreach(pictureUrl => product ! AddPicture(ProductId(productId.toString), pictureUrl))
          complete {
            StatusCodes.OK
          }
        }
      }
    }
  }
}
