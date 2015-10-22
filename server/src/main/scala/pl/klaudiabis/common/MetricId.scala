package pl.klaudiabis.common

import java.util.UUID

case class MetricId(id: String) {
  override def toString: String = id
}

object MetricId {
  def randomId(): MetricId = MetricId(UUID.randomUUID().toString)
}
