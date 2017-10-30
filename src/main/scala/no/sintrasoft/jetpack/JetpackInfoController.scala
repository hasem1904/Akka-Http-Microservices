package no.sintrasoft.jetpack

import javax.ws.rs.core.MediaType
import javax.ws.rs.{Path, Produces}

import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import no.sintrasoft.health.Status

import scala.concurrent.Future

/**
  * The JetpackInfoController provides an info endpoint with following JSON structure:
  * {
  * "app": {
  *   "version": "1.0.0",
  *   "description": "Akka Http Microserivce Demo Application",
  *   "name": "Akka Http Microservice Application"
  *   }
  * }
  */
@Produces(Array[String]{MediaType.APPLICATION_JSON})
@Singleton
@Path("/akka-http-microservice")
@Api(value = "/akka-http-microservice")
class JetpackInfoController @Inject()(@Named("version") version: String, @Named("description") description: String, @Named("name") name: String) {
  @Path("/info")
  @ApiOperation(httpMethod = "GET", response = classOf[String], value = "Returns a info status.")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Info resource not found")))
  def info: Future[App] = Future.successful(App(Status.UP.code, Application(version, description, name)))
}

case class App(status: String, application: Application)
case class Application(version: String, description: String, name: String)
