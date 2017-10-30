package no.sintrasoft.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import no.sintrasoft.config.Bindings
import no.sintrasoft.logger.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Success

/**
  * Client using the Request-Level API (Akka-Http).
  * Running GET request to "api.github.com/repos/akka/akka-http"
  *
  * http://doc.akka.io/docs/akka-http/10.0.9/scala/http/common/index.html
  * http://doc.akka.io/docs/akka-http/10.0.8/scala/http/common/uri-model.html#parsing-a-uri-string
  */
object RequestLevelClient extends App with Logger {

  val injector = Guice.createInjector(new Bindings)
  protected implicit val system: ActorSystem = injector.getInstance(classOf[ActorSystem])
  protected implicit val materializer: ActorMaterializer = injector.getInstance(classOf[ActorMaterializer])
  protected implicit val executionContext: ExecutionContext = system.dispatcher

  val akkaToolkitRequest = HttpRequest(GET, uri = "https://api.github.com/repos/akka/akka-http")
  val responseFuture = Http().singleRequest(akkaToolkitRequest)

  responseFuture.andThen {
    case Success(response) =>
      response.entity.toStrict(5 seconds).map(_.data.decodeString("UTF-8")).andThen {
        case Success(json) =>
          val pattern = """.*"open_issues":(.*?),.*""".r
          pattern.findAllIn(json).matchData foreach { m =>
            logger.info(s"There are ${m.group(1)} open issues in Akka Http.")
            //Http().shutdownAllConnectionPools().onComplete { _ =>
              system.terminate()
              materializer.shutdown()
            //}
            //system.terminate()
          }
        case _ =>
      }
    case _ => logger.error(s"request failed")
  }
}
