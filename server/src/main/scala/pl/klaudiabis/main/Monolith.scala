package pl.klaudiabis.main

import pl.klaudiabis.product.{ProductBoot, ProductService}

trait Monolith extends ProductService {

  def route = {
    val product = ProductBoot.boot
    productRoute(product.productProcessor, product.product)
  }

}
