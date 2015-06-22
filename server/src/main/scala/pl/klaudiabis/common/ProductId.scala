package pl.klaudiabis.common

import java.util.UUID

case class ProductId(id: UUID) extends AnyVal {
  override def toString: String = id.toString
}

object ProductId {
  def randomId(): ProductId = ProductId(UUID.randomUUID())

  def apply(s: String): ProductId = ProductId(UUID.fromString(s))
}

