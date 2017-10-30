package no.sintrasoft.Metrics

import no.sintrasoft.logger.Logger

class SintraSoftMetricsConfigProducer(serviceId:String) extends MetricsConfigProducer with Logger{

  logger.info(s"serviceId: $serviceId")
}
