package pl.klaudiabis.common

import akka.util.Timeout

object Timeouts {
  import scala.concurrent.duration._

  object defaults {
    implicit val defaultTimeout = Timeout(3.seconds)
    implicit val contactWorkTimeout = 10.seconds
    implicit val defaultContactTimeout = 15.seconds
    implicit val registerInterval = 10.seconds
  }

}
