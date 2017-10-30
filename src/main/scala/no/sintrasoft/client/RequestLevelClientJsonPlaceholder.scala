package no.sintrasoft.client

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.Uri.Path
import akka.stream.scaladsl.{Flow, Sink, Source}
import org.json4s.JValue
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import no.sintrasoft.akka.http.serialization.JsonSerialization
import no.sintrasoft.config.Bindings
import no.sintrasoft.logger.Logger
import org.json4s.jackson.JsonMethods._
import org.json4s.native.Serialization.write
import org.json4s.{DefaultFormats, _}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success


/**
  * Client using the Request-Level API (Akka-Http).
  * Shows various ways of creating an Akka Http Client and making
  * POST and GET HttpRequests.
  *
  *
  * Running GET request to "http://jsonplaceholder.typicode.com/users/2"
  * Parse a json string into a case class with Json4s.
  *
  * http://alexkuang.com/blog/2016/04/26/writing-an-api-client-with-akka-http/
  * http://doc.akka.io/docs/akka-http/10.0.9/scala/http/common/index.html
  * http://doc.akka.io/docs/akka-http/10.0.8/scala/http/common/uri-model.html#parsing-a-uri-string
  */
object RequestLevelClientJsonPlaceholder extends App with JsonSerialization with Logger {

  // Type alias for readability's sake
  type ClientApiResult[T] = Either[ApiError, T]
  sealed trait ApiError
  case class NotFound(error: String) extends ApiError
  case class Unauthorized(error: String) extends ApiError
  case class UnexpectedStatusCode(status: StatusCode) extends ApiError

  private val injector = Guice.createInjector(new Bindings)
  protected implicit val system: ActorSystem = injector.getInstance(classOf[ActorSystem])
  protected implicit val materializer: ActorMaterializer = injector.getInstance(classOf[ActorMaterializer])
  protected implicit val executionContext: ExecutionContext = system.dispatcher
  protected implicit val formats: DefaultFormats.type = DefaultFormats

  /**
    * POST request with Akka Http Stream
    * The source as single element and is an http request.
    * The flow describes how to send the http request (source).
    * And the runWith(Sink.head) runs the flow and returns a future of the response.
    *
    * @return a HttpResponse
    */
  val linkDbUserData = Users(s"peter@warren.com", "nb-No", "peter123", TransientPhoneNumber("Home", "41667093", "+47", true))
  logger.info( write(linkDbUserData))
  //val httpHeader = Seq(MediaRange(ContentTypes.`application/json`))
  def requestPostAkkaHttpStream: Future[HttpResponse] = {
    val source: Source[HttpRequest, NotUsed] = Source.single(
      HttpRequest(POST, uri = Uri(path = Path("/users")), entity = HttpEntity(ContentTypes.`application/json`, write(linkDbUserData)))

    )
    val flow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = Http().outgoingConnection(host="localhost", port=8080)
    source.via(flow).runWith(Sink.head)
  }

  /**
    * Handling of POST request with Akka Http Stream
    */
  val futurePost = requestPostAkkaHttpStream
  futurePost.onComplete {
    case Success(response) =>
      logger.info(s"> Json Response (Post Akka Http Stream)")
      deserializeStatusCodes(response)
      //system.terminate()
      //materializer.shutdown()
    case _ => logger.error(s"request failed")
  }

  /**
    * GET request with Akka Http Stream
    *
    * @return a HttpResponse
    */
  def requestGetAkkaHttpStream: Future[HttpResponse] = {
    val source: Source[HttpRequest, NotUsed] = Source.single(HttpRequest(GET, uri = Uri(path = Path("/users/2"))))
    val flow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = Http().outgoingConnectionHttps("jsonplaceholder.typicode.com")
    source.via(flow).runWith(Sink.head)
  }

  /**
    * Handling of GET request with Akka Http Stream
    */
  val futureGet = requestGetAkkaHttpStream
  futureGet.onComplete {
    case Success(response) =>
      getResponseBody(response).andThen {
        case Success(json) =>
          //Parse a json string into a case class with Json4s
          logger.info(s"Json Response (Get Akka Http Stream): \n$json")
          logger.info("-------------------------------")
          logger.info(s"> Parsing Json with Json4s:")
          logger.info("-------------------------------")
          val jsValue: JValue = parse(json)
          val user = jsValue.extract[User]
          logger.info(s"> name:     ${user.name}")
          logger.info(s"> username: ${user.username}")
          logger.info(s"> Address:  ${user.address}")
          logger.info(s"> Phone:    ${user.phone}")
          logger.info("----------------------------")
        //system.terminate()
        //materializer.shutdown()
        case _ =>
      }
    case _ => logger.error(s"request failed")
  }


  /**
    * The getResponseBody get the response body out of the HttpResponse object.
    *
    * @param response object containing the response body.
    * @return Future[String] that represents the HttpResponse body as string wrapped inside a Future.
    */
  private def getResponseBody(response: HttpResponse): Future[String] =
    response.entity.toStrict(5 seconds).map(_.data.decodeString("UTF-8"))

  private def deserializeStatusCodes(response: HttpResponse): Unit = response.status match {
    case StatusCodes.OK => logger.info(s"> ${response.status}")
    case StatusCodes.Created => logger.info(s"> ${response.status}")
    case StatusCodes.Unauthorized => logger.error(Unauthorized(response.entity.toString).error)
    case StatusCodes.NotFound => logger.error(NotFound(response.entity.toString).error)
    case _ => logger.error(s"> ${UnexpectedStatusCode(response.status).status.defaultMessage()}: \n ${response.entity}")
  }

  /**
    * POST Request
    */
  //val userData = Posts(1, "Fantomet", "Fantomet, ånden som går")
  //logger.info(s"> User data to be POSTED: ${write(userData)}")
  val jsonPostRequest: akka.http.scaladsl.model.HttpRequest = HttpRequest(
    method = POST,
    //uri = "http://jsonplaceholder.typicode.com/posts",
    uri = "http://localhost:8080/users",
    entity = HttpEntity(ContentTypes.`application/json`, write(linkDbUserData.copy(email ="peter@warren.corp.com", transientPhoneNumber = TransientPhoneNumber("Home", "41667090", "+47", true) )))
  )
  val responsePostFuture: Future[HttpResponse] = Http().singleRequest(jsonPostRequest)
  responsePostFuture.andThen {
    case Success(response) => deserializeStatusCodes(response)
    case _ => logger.error(s"request failed")
  }

  /**
    * GET Request
    */
  val jsonGetRequest: akka.http.scaladsl.model.HttpRequest = HttpRequest(GET, uri = "http://jsonplaceholder.typicode.com/users/2")
  val responseGetFuture: Future[HttpResponse] = Http().singleRequest(jsonGetRequest)
  responseGetFuture.andThen {
    case Success(response) =>
      response.entity.toStrict(5 seconds).map(_.data.decodeString("UTF-8")).andThen {
        case Success(json) =>
          //Parse a json string into a case class with Json4s
          logger.info(s"Json Response: \n$json")
          logger.info("----------------------------")
          logger.info(s"> Parsing Json with Json4s:")
          logger.info("----------------------------")
          val jsValue: JValue = parse(json)
          val user = jsValue.extract[User]
          logger.info(s"> name:     ${user.name}")
          logger.info(s"> username: ${user.username}")
          logger.info(s"> Address:  ${user.address}")
          logger.info(s"> Phone:    ${user.phone}")
          logger.info("----------------------------")
        //--------------------------
        //TODO: Better method for shutting down system =>
        //TODO: error => https://stackoverflow.com/questions/42181378/akka-http-client-system-shutdown-produce-outgoing-request-stream-error-akka
        //system.terminate()
        //materializer.shutdown()
        case _ =>
      }
    case _ => logger.error(s"request failed")
  }
}

//Linkdb
case class Users(email:String, language:String, password:String,transientPhoneNumber:TransientPhoneNumber)
case class TransientPhoneNumber(description:String, number:String, prefix:String, smsCapable:Boolean)

case class Posts(userId: Int, title: String, body: String)
case class User(id: String, name: String, username: String, address: Address, phone: String, website: String, company: Company)
case class Address(street: String, suite: String, city: String, zipcode: String, geo: Geo)
case class Geo(lat: String, lng: String)
case class Company(name: String, catchPhrase: String, bs: String)