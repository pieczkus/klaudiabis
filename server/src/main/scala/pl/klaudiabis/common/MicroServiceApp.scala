package pl.klaudiabis.common

import akka.http.scaladsl.server.{RouteConcatenation, Route}
import pl.klaudiabis.common.MicroServiceApp.BootedNode.{Con, RestApi}

import scala.concurrent.ExecutionContext

object MicroServiceApp {

  object BootedNode {
    val empty: BootedNode = new BootedNode {}
    type RestApi = ExecutionContext => Route

    case class Con(api1: RestApi, api2: RestApi) extends BootedNode with RouteConcatenation {
      override lazy val api = Some({ ec: ExecutionContext => api1(ec) ~ api2(ec)})
    }
  }

  trait BootedNode {
    def api: Option[RestApi] = None

    def +(that: BootedNode): BootedNode = (this.api, that.api) match {
      case (Some(r1), Some(r2)) => Con(r1, r2)
      case (Some(r1), None) => this
      case (None, Some(r2)) => that
      case _ => this
    }
  }

}
