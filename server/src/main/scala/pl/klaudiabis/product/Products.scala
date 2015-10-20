package pl.klaudiabis.product

import pl.klaudiabis.common.ProductId

case class ProductSummary(productId: Option[ProductId], name: String, year: Int, thumbnailUrl: String) {

  def withId(productId: ProductId) = copy(productId = Some(productId))

}

case class Products(products: List[ProductSummary]) extends AnyVal {

  def withNewProduct(product: ProductSummary): Products = copy(products = products :+ product)

  def recover(recoveredProducts: List[ProductSummary]): Products = copy(products = recoveredProducts)

  def get(productId: ProductId): Option[ProductSummary] = products.find(_.productId == productId)

  def get(name: String): Option[ProductSummary] = products.find(_.name == name)

  def fromYear(year: Int): List[ProductSummary] = products.filter(_.year == year)

  def years(): List[Int] = products.map(_.year)

}

object Products {
  val empty: Products = Products(List.empty)
}
