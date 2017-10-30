package no.sintrasoft.health

import com.google.inject.Inject

import scala.concurrent.Future

/**
  * Strategy interface used to provide an indication of application health.
  *
  * @author Dave Syer
  * @see ApplicationHealthIndicator
  */
/*trait HealthIndicator{
  def health: Health

  //def health: Future[(String, Any)]
  //def status: Future[(String, Any)]
}*/

case class Application(name: String, status: String)

class HealthIndicator  (val name: String, val value: Application) {
  def health: Future[(String, Any)] = Future.successful((name, value))

  def status: Future[(String, Any)] = Future.successful(("status", value.status))
}
