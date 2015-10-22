package pl.klaudiabis.common

import spray.json.DefaultJsonProtocol

trait CommonProtocols extends DefaultJsonProtocol {

  implicit val productIdFormat = jsonFormat1(ProductId.apply)

}
