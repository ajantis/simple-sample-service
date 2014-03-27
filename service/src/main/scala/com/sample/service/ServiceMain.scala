package com.sample.service

import java.util.concurrent.TimeUnit

import scala.util.control.Exception.catching
import scala.concurrent.duration._

import akka.actor.{ Props, ActorSystem }
import akka.io.IO

import spray.can.Http

import com.typesafe.config.{ ConfigException, Config, ConfigFactory }

import http.ApiActor

object ServiceMain extends App {
  implicit val system = ActorSystem("simple-service")

  catching(classOf[ConfigException]).either(Settings(ConfigFactory.load)) match {

    case Right(settings) =>
      startup(system, settings)

    case Left(error) =>
      println(s"Invalid configuration. Details: ${error.getMessage}")
      system.shutdown()
      system.awaitTermination(2 seconds)
      System.exit(1)
  }

  private[this] def startup(implicit system: ActorSystem, settings: Settings): Unit = {
    val apiActor = system.actorOf(Props(new ApiActor(settings)))

    IO(Http) ! Http.Bind(apiActor, interface = settings.Host, port = settings.Port)

    println(s"Simple service is started. Listening on ${settings.Host}:${settings.Port}")
  }
}

case class Settings(private val config: Config) {
  val Host = config.getString("service.http.host")
  val Port = config.getInt("service.http.port")
  val Timeout = FiniteDuration(config.getDuration("service.processing.timeout", TimeUnit.SECONDS), TimeUnit.SECONDS)
}
