package pl.klaudiabis.common

import java.util.UUID

case class UserId(id: String) {
  override def toString: String = id
}

object UserId {
  def randomId(): UserId = UserId(UUID.randomUUID().toString)
}
