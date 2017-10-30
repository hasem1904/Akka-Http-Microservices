package no.sintrasoft.controller

//import javax.validation.Path

import javax.ws.rs.{Path, PathParam}

import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import io.swagger.annotations._
import no.sintrasoft.domain.{MessageRequest, SampleClient}
import no.sintrasoft.logger.Logger

import scala.concurrent.ExecutionContext
@Singleton
@Path("/akka-http-microservice")
@Api(value = "/akka-http-microservice")
class AkkaHttpController @Inject()(@Named("serviceName") serviceName: String, implicit val context: ExecutionContext) extends Logger{

  @Path("/welcome")
  @ApiOperation(httpMethod = "GET", response = classOf[String], value = "Returns a welcome message.")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Welcome message not found")))
  def welcome: String = {
    s"Welcome to $serviceName!"
  }

  @Path("/welcome")
  @ApiOperation(httpMethod = "POST", response = classOf[no.sintrasoft.domain.MessageRequest], value = "Returns a welcome message.")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "request", required=true, dataType = "no.sintrasoft.domain.MessageRequest", value="The message to include in the welcome message")))
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Welcome message not found")))
  def welcomeMessage(request: MessageRequest) = {
    s"Welcome to $serviceName: ${request.message}!"
  }

  @Path("/client/{name}")
  @ApiOperation(httpMethod = "GET", response = classOf[SampleClient], value = "Returns a client with the specified name.")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "name", required=true, dataType = "string", /*paramType = "path",*/ value="The name of the client to lookup")))
  @ApiResponses(Array(new ApiResponse(code = 500, message = "Error retrieving client data")))
  def getClientByName(@PathParam("name") name : String) = {
    //SampleClientDao.getClientByName(name)
    SampleClient(155, name)
  }
}
