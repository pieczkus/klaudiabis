package pl.klaudiabis.metrics

import pl.klaudiabis.common.CommonProtocols

trait MetricProtocols extends CommonProtocols {

  implicit val metricFormat = jsonFormat1(Metric.apply)

}
