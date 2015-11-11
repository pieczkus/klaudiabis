package pl.klaudiabis.main

import akka.http.scaladsl.server._
import pl.klaudiabis.contact.{ContactService, ContactBoot}
import pl.klaudiabis.metrics.{MetricsService, MetricsBoot}
import pl.klaudiabis.product.{ProductBoot, ProductService}

trait Monolith extends RouteConcatenation with ProductService with MetricsService with ContactService {

  def route: Route = {
    val product = ProductBoot.boot
    val metrics = MetricsBoot.boot
    val contact = ContactBoot.boot
    productRoute(product.productProcessor, product.product, metrics.metricsSummary) ~
      metricsRoute(metrics.metricsSummary, metrics.productMetric) ~
      contactRoute(contact.masterProxy)
  }

}
