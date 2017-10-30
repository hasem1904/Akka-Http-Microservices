package no.sintrasoft.akka.http.routes

import akka.http.scaladsl.server.Route
import com.google.inject.Singleton
import no.sintrasoft.logger.Logger

@Singleton
class DebuggingRoutes extends Logger{
  def apply(): Route = {
  logger.info(s"> Setting up DebuggingRoutes")
  ???
}
}
