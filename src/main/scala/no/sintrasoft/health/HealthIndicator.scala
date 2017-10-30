package no.sintrasoft.health

import scala.concurrent.Future

case class Application(name: String, status: String)

class HealthIndicator  (val name: String, val value: Application) {
  def health: Future[(String, Any)] = Future.successful((name, value))

  def status: Future[(String, Any)] = Future.successful(("status", value.status))
}
