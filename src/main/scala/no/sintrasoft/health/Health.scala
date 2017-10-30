package no.sintrasoft.health

import scala.collection.mutable

class Health(val status: Status, val details: mutable.Map[String, Any]) {
  override def toString = s"Health($status, $details)"
}

object Health {
  //Factory methods (apply)...
  def apply(): Health = new Health(Status.UNKNOWN, mutable.Map.empty[String, Any])

  def apply(status: Status, details: mutable.Map[String, Any]): Health = {
    require(Option(status).isDefined, "Status must not be null")
    require(Option(details).isDefined, "Details must not be null")
    new Health(status, details)
  }

  def apply(status: Status): Health = {
    require(Option(status).isDefined, "Status must not be null")
    new Health(status, mutable.Map.empty[String, Any])
  }

  val Up = Health(Status.UP)
  val Down = Health(Status.DOWN)
  def Down(ex:Exception): Health = withException(ex)
  val OutOfService = Health(Status.OUT_OF_SERVICE)

  /**
    * Record detail for given {@link Exception}.
    *
    * @param ex the exception
    * @return this { @link Health} instance
    */
  def withException(ex: Exception): Health = {
    require(Option(ex).isDefined, "Exception must not be null")
    withDetail("error", ex.getClass.getName + ": " + ex.getMessage)
  }

  /**
    * Record detail using given {@code key} and {@code value}.
    *
    * @param key   the detail key
    * @param value the detail value
    * @return this { @link Health} instance
    */
  def withDetail(key: String, value: Any): Health = {
    require(Option(key).isDefined, "Key must not be null")
    require(Option(value).isDefined, "Value must not be null")
    val health = Health.apply()
   // health.details(key -> value)
    health
  }
}
