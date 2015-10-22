package pl.klaudiabis.main

import akka.http.scaladsl.server._
import pl.klaudiabis.metrics.{MetricsService, MetricsBoot}
import pl.klaudiabis.product.{ProductBoot, ProductService}

trait Monolith extends RouteConcatenation with ProductService with MetricsService {

  def route: Route = {
    val product = ProductBoot.boot
    val metrics = MetricsBoot.boot
    productRoute(product.productProcessor, product.product, metrics.metricsSummary) ~
      metricsRoute(metrics.metricsSummary, metrics.productMetric)
  }

}
