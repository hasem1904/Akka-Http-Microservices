package no.sintrasoft.logger

import org.slf4j.LoggerFactory

trait Logger {
  val logger = LoggerFactory.getLogger(s"no.sintrasoft.${this.getClass.getCanonicalName}")
}
