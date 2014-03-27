package com.sample.service
package processor

import spray.json._

case class ProcessingError(msg: String, reason: Option[Throwable] = None)

object ProcessingError {
  implicit val jsonWriter = new RootJsonWriter[ProcessingError] {
    override def write(e: ProcessingError): JsValue = JsObject("error" -> JsString(e.msg))
  }
}
