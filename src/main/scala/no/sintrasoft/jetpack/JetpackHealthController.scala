package no.sintrasoft.jetpack

import java.util
import javax.ws.rs.core.MediaType
import javax.ws.rs.{Path, Produces}

import com.codahale.metrics.health.HealthCheck
import com.google.inject.{Guice, Inject, Injector, Singleton}
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import no.sintrasoft.config.Bindings
import no.sintrasoft.health.{DiskSpaceHealthIndicator, HealthIndicator}
import no.sintrasoft.logger.Logger
import org.json4s.{DefaultFormats, JValue}

import scala.concurrent.{ExecutionContext, Future}

@Produces(Array[String]{MediaType.APPLICATION_JSON})
@Singleton
@Path("/akka-http-microservice")
@Api(value = "/akka-http-microservice")
class JetpackHealthController(healthIndicators: Set[HealthIndicator])(implicit val executionContext: ExecutionContext) extends Logger{
  lazy val indicators: Seq[HealthIndicator] = healthIndicators.toSeq
  lazy val applicationHealthIndicator: HealthIndicator = indicators.filter(_.name.equalsIgnoreCase("application")).head

  val injector: Injector = Guice.createInjector(new Bindings)
  val diskSpaceHealthIndicator: DiskSpaceHealthIndicator = injector.getInstance(classOf[DiskSpaceHealthIndicator])
  //val registry: HealthCheckRegistry = injector.getInstance(classOf[HealthCheckRegistry])

  implicit val formats: DefaultFormats.type = DefaultFormats

  @Path("/health")
  @ApiOperation(httpMethod = "GET", response = classOf[String], value = "Returns a health status.")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Health resource not found")))
  def health: Future[(String, JValue)] = {
    val results: util.SortedMap[String, HealthCheck.Result] = diskSpaceHealthIndicator.registry.runHealthChecks()
    import scala.collection.JavaConversions._
    //import collection.JavaConverters._
    Future{
      import org.json4s.native.JsonMethods._
      "diskSpace" -> parse(results.head._2.getMessage)
    }
  }

  def applicationStatusEntry: Future[(String, Any)] = {
    for {
      status <- applicationHealthIndicator.status
    } yield ("status", status)
  }

  @Path("/detailedHealth")
  @ApiOperation(httpMethod = "GET", response = classOf[String], value = "Returns a detailed health status.")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Detailed Health resource not found")))
  def detailedHealth = {
    val detailsFutureSeq: Future[Seq[(String, Any)]] =
      Future.sequence(indicators.map {
      ind =>
        for {
          details <- ind.health
        } yield (ind.name, details)
    })
    for {
      applicationStatusEntry <- applicationStatusEntry
      detailsSeq <- detailsFutureSeq
    } yield (applicationStatusEntry +: detailsSeq).toMap
  }
}
