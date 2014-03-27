package com.sample.service
package http

import akka.actor.{ ActorRef, Props, ActorLogging }

import spray.routing.{ RequestContext, Route, HttpService, HttpServiceActor }
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._

import domain.SimpleJob
import processor.ProcessingActor

class ApiActor(settings: Settings) extends HttpServiceActor with HttpService with ActorLogging {
  import ApiActor._
  import ProcessingActor._

  private[http] val timeout = settings.Timeout

  def receive = runRoute(route)

  private[service] def route: Route = {
    path(Ping) {
      get {
        complete(StatusCodes.OK -> "pong")
      }
    } ~
      pathPrefix(Api) {
        path(Service) {
          post {
            entity(as[SimpleJob]) { job =>
              requestContext =>
                val processor = createProcessor(requestContext)
                processor ! job
            }
          }
        }
      }
  }

  private[service] def createProcessor(ctx: RequestContext): ActorRef =
    context.actorOf(Props(new ProcessingActor(timeout, resultCallback = completeJobRequest(ctx))))

  private[service] def completeJobRequest(ctx: RequestContext)(result: JobResult): Unit = {
    result match {
      case Right(job)  => ctx.complete(StatusCodes.Accepted -> job)

      case Left(error) => ctx.complete(StatusCodes.ServiceUnavailable -> error)
    }
  }
}

object ApiActor {
  private[service] val Ping = "ping"
  private[service] val Api = "api"
  private[service] val Service = "service"
}
