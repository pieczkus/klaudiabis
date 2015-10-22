package pl.klaudiabis.product

import pl.klaudiabis.common.{CommonProtocols, ProductId}
import spray.json.DefaultJsonProtocol

trait ProductProtocols extends CommonProtocols {

  implicit val productSummaryFormat = jsonFormat4(ProductSummary.apply)

}
