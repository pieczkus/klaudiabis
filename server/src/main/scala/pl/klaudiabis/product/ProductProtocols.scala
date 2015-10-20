package pl.klaudiabis.product

import pl.klaudiabis.common.ProductId
import spray.json.DefaultJsonProtocol

trait ProductProtocols extends DefaultJsonProtocol {

  implicit val productIdFormat = jsonFormat1(ProductId.apply)

  implicit val productSummaryFormat = jsonFormat4(ProductSummary.apply)

}
