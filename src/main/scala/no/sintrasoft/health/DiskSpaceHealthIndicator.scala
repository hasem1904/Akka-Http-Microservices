package no.sintrasoft.health

import java.io.File

import com.codahale.metrics.health.HealthCheck.Result
import com.google.inject.Inject
import com.google.inject.name.Named
import nl.grons.metrics.scala.DefaultInstrumented
import no.sintrasoft.logger.Logger
import org.json4s._
import org.json4s.native.Serialization.write
/**
  * The DiskSpaceHealthIndicator should provide the following json structure:
  *
  * "diskSpace" : {
  * "status" : "UP",
  * "free" : 209047318528,
  * "threshold" : 10485760
  * }
  */
class DiskSpaceHealthIndicator @Inject()(@Named("threshold") threshold: Long) extends DefaultInstrumented with Logger {

  implicit val formats: DefaultFormats.type = DefaultFormats

  // Define a health check
  healthCheck("diskSpace") {
    val file: File = new File(".")
    val free: Long = file.getFreeSpace
    if (free < threshold) {
      //Result text as json...
      Result.unhealthy(write(DiskspaceStatus(Status.UP.code, free, threshold)))
    }
    else {
      Result.healthy(write(DiskspaceStatus(Status.UP.code, free, threshold)))
    }
  }
}

//https://commitlogs.com/2017/01/14/serialize-deserialize-json-with-json4s-in-scala/
case class DiskspaceStatus(status: String, free: Long, threshold: Long){
  override def toString = super.toString
}


