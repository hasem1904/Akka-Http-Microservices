name := "Akka-Http-Microservices"

version := "0.1"

scalaVersion := "2.12.3"

val akkaHttpVersion = "10.0.9"
val akkaHttpCoreVersion = "10.0.9"
val akkaActorVersion = "2.4.19"
val akkaStreamVersion = "2.0.2"
val swaggerAkkaHttpVersion = "0.11.0"
val swaggerUiAkkaHttpVersion = "1.1.0"
val json4sVersion = "3.5.3"
val scalaTestVersion = "10.0.6"
val akkaSlf4jVersion = "2.4.19"
val logbackClassicVersion = "1.2.3"
val googleGuiceVersion = "3.0"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xlint",
  "-Ywarn-unused",
  "-Ywarn-dead-code",
  "-feature",
  "-language:_"
)

libraryDependencies ++= Seq(
  // -- Akka-Http --
  "com.typesafe.akka" % "akka-http_2.12" % akkaHttpVersion,
  "com.typesafe.akka" % "akka-http-core_2.12" % akkaHttpCoreVersion,

  // -- Akka-Actor --
  "com.typesafe.akka" % "akka-actor_2.12" % akkaActorVersion,

  // -- Swagger --
  "com.github.swagger-akka-http" % "swagger-akka-http_2.12" % swaggerAkkaHttpVersion,
  "co.pragmati" %% "swagger-ui-akka-http" % swaggerUiAkkaHttpVersion,

  // -- Json4s --
  "org.json4s" % "json4s-native_2.12" % json4sVersion,
  "org.json4s" % "json4s-jackson_2.12" % json4sVersion,
  "org.json4s" % "json4s-ext_2.12" % json4sVersion,

  // -- Google Guice --
  "com.google.inject" % "guice" % googleGuiceVersion,

  // -- SLF4J Logging --
  "com.typesafe.akka" % "akka-slf4j_2.12" % akkaSlf4jVersion,
  "ch.qos.logback" % "logback-classic" % logbackClassicVersion,

  // -- Metrics --
  //"com.codahale.metrics" % "metrics-core" % "3.0.2"
  "nl.grons" %% "metrics-scala" % "3.5.9_a2.4"
)
