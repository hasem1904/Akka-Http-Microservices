package no.sintrasoft.route

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpResponse, _}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import com.google.inject.name.Named
import no.sintrasoft.akka.http.routes.{DebuggingRoutes, JetpackRoutes, SwaggerRoutes}
import no.sintrasoft.akka.http.serialization.JsonSerialization
import no.sintrasoft.controller.{AkkaHttpController, FeatureController}
import no.sintrasoft.domain.MessageRequest
import no.sintrasoft.logger.Logger


class Routes @Inject()(
                        @Named("Service") private val service: String = "akka-http-microservice",
                        private val akkaHttpController: AkkaHttpController,
                        private val featureController: FeatureController,
                        private val jetpackRoutes: JetpackRoutes,
                        private val debuggingRoutes: DebuggingRoutes,
                        private val swaggerRoutes: SwaggerRoutes,
                        private val sys: ActorSystem)(private implicit val materializer: ActorMaterializer) extends JsonSerialization with Logger {

  //TODO: Fix this!
  val exceptionHandler = ExceptionHandler {
    case _: ArithmeticException =>
      extractUri { uri =>
        logger.error(s"Request to $uri could not be handled normally")
        complete(HttpResponse(InternalServerError, entity = "Bad numbers, bad result!!!"))
      }
  }


  def apply(): Route = {
    logger.info(">Setting up routes...")
    handleExceptions(exceptionHandler) {
      jetpackRoutes() ~
        debuggingRoutes() ~
        swaggerRoutes() ~
        //The 'service' name her in the URL is a SintraSoft best practice, facilitate alternative routing mechanisms
        pathPrefix("api" / "v1" / service) {v1Routes()}
    }
  }
  private def v1Routes(): Route  = {
     logger.info(s"Calling Route: v1Routes")
      path("welcome") {
        get { complete {akkaHttpController.welcome}} ~
          (post & entity(as[MessageRequest])) { messageRequest => complete (akkaHttpController.welcomeMessage(messageRequest))}} ~
      path("client" / Segment) { clientName =>get {complete(akkaHttpController.getClientByName(clientName))} ~
      path("features") { get {complete(featureController.getAllFeatures)}}
      }
  }
}
