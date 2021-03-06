package pl.klaudiabis.common

import java.util.UUID

case class ProductId(id: String) {

  def metricId = {
    UUID.fromString(id).getLeastSignificantBits.toString
  }

  override def toString: String = id
}

object ProductId {
  def randomId(): ProductId = ProductId(UUID.randomUUID().toString)
}

