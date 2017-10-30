package no.sintrasoft.health

abstract class AbstractHealthIndicator /*extends HealthIndicator("Test", Application("Test", "Up"))*/ {

  /*override def health: Health = {
    val health = Health.apply()
    try
      doHealthCheck(health)
    catch {
      case ex: Exception =>
        Health.Down(ex)
    }
    health
  }*/

  /**
    * Actual health check logic.
    *
    * @param builder the { @link Builder} to report health status and details
    * @throws Exception any { @link Exception} that should create a { @link Status#DOWN}
    *                   system status.
    */
  @throws[Exception]
  protected def doHealthCheck(builder: Health): Unit
}
