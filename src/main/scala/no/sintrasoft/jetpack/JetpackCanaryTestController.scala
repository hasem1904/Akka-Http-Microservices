package no.sintrasoft.jetpack

import scala.concurrent.Future

class JetpackCanaryTestController {
  def testConfiguration: Future[String] = Future.successful("JetpackCanaryTestController")
}
