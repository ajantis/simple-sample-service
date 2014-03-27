package com.sample.service
package processor

import java.security.MessageDigest

import scala.util.control.Exception._
import scala.util.Random

import akka.actor.{ Actor, ActorLogging }

import domain.SimpleJob
import ProcessingActor._

class Worker extends Actor with ActorLogging {

  def receive = {
    case job: SimpleJob => sender ! runProcessing(job)
  }

  private[processor] def runProcessing(job: SimpleJob): JobResult = {
    handling(classOf[Throwable]).by(t => Left(toProcessingException(t))) {
      val result = job.copy(lines = process(job.lines))
      Right(result)
    }
  }

  @throws[Exception]("This processing is unstable sometimes")
  private[this] def process(lines: Vector[String]): Vector[String] = {
    val start = System.currentTimeMillis()

    // delay is proportional to a number of lines
    Thread.sleep(Random.nextInt(10) * 100 * lines.size) // (0 <-> 900) * N ms -> for N lines

    val end = System.currentTimeMillis()

    log.debug(s"Processing took ${end - start} ms to complete")

    // 10% probability of failure
    if (Random.nextInt(10) < 1) {
      log.error("Worker gone mad!")
      throw new Exception("Something went wrong!")
    } else {
      // Let's calculate some hashes!
      lines.map(s => MessageDigest.getInstance("MD5").digest(s.getBytes).map(0xFF & _)
        .map { "%02x".format(_) }.foldLeft("") { _ + _ })
    }
  }

  private[this] def toProcessingException(t: Throwable): ProcessingError = ProcessingError(t.getMessage, Some(t))
}
