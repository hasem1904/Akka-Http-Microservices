package no.sintrasoft.akka.http.routes


import akka.http.scaladsl.server.Route
import com.google.inject.{Inject, Singleton}
import no.sintrasoft.akka.http.serialization.JsonSerialization
import no.sintrasoft.jetpack.{JetpackCanaryTestController, JetpackHealthController, JetpackInfoController}
import akka.http.scaladsl.server.Directives._
import no.sintrasoft.logger.Logger

@Singleton
class JetpackRoutes @Inject()(
 protected val jetpackHealthController: JetpackHealthController,
 protected val jetpackInfoController: JetpackInfoController,
 protected val jetpackCanaryController: JetpackCanaryTestController) extends JsonSerialization with Logger{

  def apply(): Route = {
    logger.info("> Setting up JetpackRoutes")
    pathPrefix("akka-http-microservice") {
      path("health") { get { complete {jetpackHealthController.health}}} ~
      path("detailedHealth") {get {complete {jetpackHealthController.detailedHealth}}} ~
      path("info") {get {complete {jetpackInfoController.info}}} ~
      path("test") {get {complete {jetpackCanaryController.testConfiguration}}}
    }
  }
}
