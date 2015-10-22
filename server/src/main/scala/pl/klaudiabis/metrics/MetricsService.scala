package pl.klaudiabis.metrics


import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.Materializer
import pl.klaudiabis.common.{CommonProtocols, ProductId}
import pl.klaudiabis.metrics.ProductViewMetric.GetAllViews
import pl.klaudiabis.metrics.ProductViewSummary.GetViewsSummary

import scala.concurrent.ExecutionContextExecutor

trait MetricsService extends SprayJsonSupport with CommonProtocols {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  import akka.pattern.ask
  import pl.klaudiabis.common.Timeouts.defaults._

  def metricsRoute(metricsSummary: ActorRef, productMetric: ActorRef): Route = {
    logRequest("metrics-microservice") {
      pathPrefix("metrics") {
        (get & path(JavaUUID)) { productId =>
          complete {
            (productMetric ? GetAllViews(ProductId(productId.toString))).mapTo[Map[String, Long]]
          }
        } ~ get {
          complete {
            (metricsSummary ? GetViewsSummary).mapTo[Map[String, Long]]
          }
        }
      }
    }
  }

}
