package com.sample.service
package domain

import spray.json._
import DefaultJsonProtocol._

case class SimpleJob(id: String, lines: Vector[String])

object SimpleJob {
  implicit val jsonFormat = jsonFormat2(SimpleJob.apply)
}
