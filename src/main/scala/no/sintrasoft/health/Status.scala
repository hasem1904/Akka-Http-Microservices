package no.sintrasoft.health

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonInclude, JsonProperty}

/**
  * Value object to express state of a component or subsystem.
  * <p>
  * Status provides convenient constants for commonly used states like {@link #UP},
  * {@link #DOWN} or {@link #OUT_OF_SERVICE}.
  * <p>
  * Custom states can also be created and used throughout the Spring Boot Health subsystem.
  *
  * @author Christian Dupuis
  * @since 1.1.0
  */
@JsonInclude(Include.NON_EMPTY)
object Status {
  /**
    * {@link Status} indicating that the component or subsystem is in an unknown state.
    */
  val UNKNOWN = new Status("UNKNOWN")
  /**
    * {@link Status} indicating that the component or subsystem is functioning as
    * expected.
    */
  val UP = new Status("UP")
  /**
    * {@link Status} indicating that the component or subsystem has suffered an
    * unexpected failure.
    */
  val DOWN = new Status("DOWN")
  /**
    * {@link Status} indicating that the component or subsystem has been taken out of
    * service and should not be used.
    */
  val OUT_OF_SERVICE = new Status("OUT_OF_SERVICE")
}

/**
  * Create a new {@link Status} instance with the given code and description.
  *
  * @param code        the status code
  * @param description a description of the status
  */
@JsonInclude(Include.NON_EMPTY)
final class Status(val code: String, val description: String) {
  require(Option(code).isDefined, "Code must not be null")
  require(Option(description).isDefined, "Description must not be null")

  /**
    * Create a new {@link Status} instance with the given code and an empty description.
    *
    * @param code the status code
    */
  def this(code: String) {
    this(code, "")
  }

  /**
    * Return the code for this status.
    *
    * @return the code
    */
  @JsonProperty("status")
  def getCode: String = this.code

  /**
    * Return the description of this status.
    *
    * @return the description
    */
  @JsonInclude(Include.NON_EMPTY)
  def getDescription: String = this.description

  override def equals(other: Any): Boolean = other match {
    case that: Status =>
      code == that.code &&
        description == that.description
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(code, description)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"Status($code, $description)"
}