package no.sintrasoft

//import com.codahale.metrics.MetricRegistry
//import com.codahale.metrics.Timer.

import com.github.swagger.akka.SwaggerSite
import com.google.inject.Guice
import com.typesafe.config.Config
import no.sintrasoft.Metrics.MetricsRegistry
import no.sintrasoft.akka.http.routes.{JetpackRoutes, SwaggerRoutes}
import no.sintrasoft.config.Bindings
import no.sintrasoft.controller.AkkaHttpController
import no.sintrasoft.health.{Application, HealthIndicator}
import no.sintrasoft.jetpack.{JetpackCanaryTestController, JetpackHealthController, JetpackInfoController}
import no.sintrasoft.logger.Logger
import no.sintrasoft.swagger.SwaggerDocService
//import no.sintrasoft.route.Routes

import _root_.akka.actor.ActorSystem
import _root_.akka.http.scaladsl.Http
import _root_.akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.io.Source
import scala.util.Failure

object Boot extends App with Logger with SwaggerSite {

  printBanner("1.0", "Development", "2.12.2", "2.4.1")

  val injector = Guice.createInjector(new Bindings)
  implicit val system: ActorSystem = injector.getInstance(classOf[ActorSystem])
  implicit val executionContext: ExecutionContext = injector.getInstance(classOf[ExecutionContext])
  implicit val materializer: ActorMaterializer = injector.getInstance(classOf[ActorMaterializer])

  val config: Config = injector.getInstance(classOf[Config])

  implicit val metricRegistry: MetricsRegistry = injector.getInstance(classOf[no.sintrasoft.Metrics.MetricsRegistry])
  //protected val metricSetup = injector.getInstance(classOf[MetricSetup])

  val healthIndicator = /*injector.getInstance(classOf[HealthIndicator])*/ new HealthIndicator("application", Application("Akka-Http-Microservice", "UP"))
  val jetpackHealthController = /*injector.getInstance(classOf[JetpackHealthController]) */new JetpackHealthController(Seq(healthIndicator).toSet)
  val jetpackInfoController: JetpackInfoController = injector.getInstance(classOf[JetpackInfoController])
  val jetpackCanaryController: JetpackCanaryTestController = injector.getInstance(classOf[JetpackCanaryTestController])
  val jetpackRoutes = new JetpackRoutes(jetpackHealthController, jetpackInfoController, jetpackCanaryController)
  val akkaHttpController = injector.getInstance(classOf[AkkaHttpController])
  val swaggerRoutes = new SwaggerRoutes(akkaHttpController)

  logger.debug("This is debug!!!")

  /** The application wide health check registry. */
  /*val healthCheckRegistry = injector.getInstance(classOf[HealthCheckRegistry])
  val diskSpaceHealthIndicator = injector.getInstance(classOf[DiskSpaceHealthIndicator])
  logger.info(s"MetricBaseName:              ${diskSpaceHealthIndicator.metricBaseName}")
  logger.info(s"Names:                       ${diskSpaceHealthIndicator.registry.getNames}")
  logger.info(s"runHealthChecks:             ${diskSpaceHealthIndicator.registry.runHealthChecks()}")
  logger.info(s"runHealthChecks().values():  ${diskSpaceHealthIndicator.registry.runHealthChecks().values()}")
  logger.info(s"runHealthChecks().entrySet():${diskSpaceHealthIndicator.registry.runHealthChecks().entrySet()}")
  logger.info(s"keySet:                      ${diskSpaceHealthIndicator.registry.runHealthChecks().keySet()}")


  val r = diskSpaceHealthIndicator.registry.runHealthChecks().values()
  val iter = r.iterator()
  while (iter.hasNext) {
    logger.info(s"> ${iter.next()}")
  }*/

  //val res = healthCheckRegistry.runHealthCheck("diskSpace")
  //logger.info(s"DiskSpaceHealthIndicator: $res")


  //val routes = injector.getInstance(classOf[Routes])
  //metricRoutes()
  /*timer(metricRegistry) {
    val routes = injector.getInstance(classOf[Routes])
    routes()
  }*/

  val route =
    SwaggerDocService.routes ~
      swaggerSiteRoute ~
      swaggerRoutes.apply() ~
      jetpackRoutes.apply()

  val serverBindingFuture = Http().bindAndHandle(route, config.getString("http.interface"), config.getInt("http.port"))
  serverBindingFuture.onComplete {
    case Failure(t) => logger.error(s"Server binding failed with ${t.getMessage}")
      system.terminate()
    case scala.util.Success(s) => logger.info(s"Server started at: ${s.localAddress.getHostName} ${s.localAddress.getPort}...")
  }

  /**
    * Prints the banner
    */
  private def printBanner(appVer: String, env: String, scalaVer: String, akkaVer: String): Unit = {
    val applicationVersion = "{application.version}"
    val applicationEnvironment = "{application.environment}"
    val scalaVersion = "{scala.version}"
    val akkaVersion = "{akka.version}"

    Source.fromInputStream(this.getClass.getResourceAsStream("/banner.txt"))
      .getLines()
      .foreach {
        case line@l if l.contains(applicationVersion) => println(line.replace(applicationVersion, appVer))
        case line@l if l.contains(applicationEnvironment) => println(line.replace(applicationEnvironment, env))
        case line@l if l.contains(scalaVersion) => println(line.replace(scalaVersion, scalaVer))
        case line@l if l.contains(akkaVersion) => println(line.replace(akkaVersion, akkaVer))
        case l => println(l)
      }
  }
}
