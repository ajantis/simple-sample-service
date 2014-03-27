package com.sample.service
package processor

import scala.concurrent.duration._

import akka.actor._

import domain.SimpleJob
import ProcessingActor._

class ProcessingActor(timeout: FiniteDuration, resultCallback: JobResult => Unit) extends Actor with ActorLogging {

  lazy val worker = createWorker

  context.setReceiveTimeout(timeout)

  override def preStart() {
    log.debug("Starting new processor...")
  }

  def receive = {
    case job: SimpleJob    => worker ! job

    case result: JobResult => finish(result)

    case ReceiveTimeout =>
      log.error("Got the timeout in processor. Aborting...")
      finishWithError(ProcessingError("Processing failed with timeout."))
  }

  private[processor] def finish(result: JobResult): Unit = {
    log.info("Job is done.")
    resultCallback(result)
    self ! PoisonPill
  }

  private[processor] def finishWithError(error: ProcessingError): Unit = {
    resultCallback(Left(error))
    self ! PoisonPill
  }

  private[processor] def createWorker: ActorRef = context.actorOf(Props(new Worker))
}

object ProcessingActor {
  type JobResult = Either[ProcessingError, SimpleJob]
}