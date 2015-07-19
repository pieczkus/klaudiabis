package pl.klaudiabis.product

import pl.klaudiabis.common.ProductId
import spray.json.DefaultJsonProtocol

trait ProductProtocols extends DefaultJsonProtocol {

  implicit val productIdFormat = jsonFormat1(ProductId.apply)

  implicit val productSummaryFormat = jsonFormat3(ProductSummary.apply)

  implicit val productPictureFormat = jsonFormat2(Picture.apply)

  implicit val productPicturesFormat = jsonFormat1(Pictures.apply)

  implicit val productDetailsFormat = jsonFormat2(ProductDetails.apply)




}
