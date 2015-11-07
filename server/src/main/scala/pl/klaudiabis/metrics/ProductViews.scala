package pl.klaudiabis.metrics

import java.time.LocalDate

import pl.klaudiabis.common.ProductId

case class ViewsSummary(productViews: Map[String, Long]) extends AnyVal {

  def view(productId: ProductId) = copy(productViews = productViews.updated(productId.toString, productViews(productId.toString) + 1))

  def get(productId: ProductId) = productViews(productId.toString)

}

object ViewsSummary {
  val empty = ViewsSummary(Map.empty.withDefaultValue(0))
}


case class ProductViews(viewsByDay: Map[String, Long]) extends AnyVal {

  def view(date: LocalDate) = copy(viewsByDay = viewsByDay.updated(date.toString, viewsByDay(date.toString) + 1))

  def get(date: LocalDate) = viewsByDay(date.toString)

}

object ProductViews {
  val empty = ProductViews(Map.empty.withDefaultValue(0))
}

case class Metric(value: Long)