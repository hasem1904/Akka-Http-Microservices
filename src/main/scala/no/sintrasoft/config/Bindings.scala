package no.sintrasoft.config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext

class Bindings extends AbstractModule {
  val service = "akka-http-microservice"
  val serviceId = "akka-http-microservice"
  val serviceName = "akka-http-microservice"

  override def configure() = {
    bindNames()
    bindConfig()
    bindAkkaActorSystem()
    //bindMetrics()
    //bindMongo()
    //bindSQL()
    //bindJetpack()
  }

  def bindNames() = {
    bind(classOf[String]).annotatedWith(Names.named("service")).toInstance(service)
    bind(classOf[String]).annotatedWith(Names.named("serviceId")).toInstance(serviceId)
    bind(classOf[String]).annotatedWith(Names.named("serviceName")).toInstance(serviceName)
  }

  def bindConfig() = {
    val config = ConfigFactory.load()
    bind(classOf[Config]).toInstance(config)
    //health.disk.threshold
    val threshold = config.getLong("health.disk.threshold")
    bind(classOf[Long]).annotatedWith(Names.named("threshold")).toInstance(threshold)
    //info.app.name
    val name = config.getString("info.app.name")
    bind(classOf[String]).annotatedWith(Names.named("name")).toInstance(name)
    //info.app.description
    val description = config.getString("info.app.description")
    bind(classOf[String]).annotatedWith(Names.named("description")).toInstance(description)
    //info.app.description
    val version = config.getString("info.app.version")
    bind(classOf[String]).annotatedWith(Names.named("version")).toInstance(version)
  }

  def bindAkkaActorSystem() = {
    implicit val actorSystem: ActorSystem = ActorSystem(s"$serviceName-actor-system")
    bind(classOf[ActorSystem]).toInstance(actorSystem)
    bind(classOf[ExecutionContext]).toInstance(actorSystem.dispatcher)
    bind(classOf[ActorMaterializer]).toInstance(ActorMaterializer())
  }

  /*def bindMetrics() = {
    //bind(classOf[MetricSetup]).toInstance(new no.sintrasoft.Metrics.SintrasoftMetricSetup)
    bind(classOf[MetricsConfigProducer]).toInstance(new SintraSoftMetricsConfigProducer(s"service.$serviceId"))
    bind(classOf[MetricsRegistry]).toInstance(new MetricsRegistry)
    bind(classOf[HealthCheckRegistry]).toInstance(new HealthCheckRegistry)
    bind(classOf[DiskSpaceHealthIndicator]).toInstance(new DiskSpaceHealthIndicator(10485760))
  }*/

  /*def bindMongo() = {
    "MongoDB"
  }

  def bindSQL() = {
    "SQL"
  }*/

  /*def bindControllers() = {
    bind(classOf[JetpackInfoController])
    bind(classOf[JetpackCanaryTestController])
    bind(classOf[AkkaHttpController])
  }
 */
  /*def bindJetpack() = {
    //val healthBinder = ScalaMultiBinder.newSetBinder[HealthIndicator](binder())
    //healthBinder.addBinding.to[ApplicationHealthIndicator]
    //healthBinder.addBinding.to[DiskSpaceHealthIndicator]
  }*/
}
