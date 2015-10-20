package pl.klaudiabis.main

import akka.http.scaladsl.server._
import pl.klaudiabis.product.{ProductBoot, ProductService}

trait Monolith extends RouteConcatenation with ProductService {

  def route: Route = {
    val product = ProductBoot.boot
    productRoute(product.productProcessor, product.product)
  }

}
