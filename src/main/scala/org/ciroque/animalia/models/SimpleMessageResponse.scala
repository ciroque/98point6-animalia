package org.ciroque.animalia.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

case class SimpleMessageResponse(message: String) extends Throwable(message) {}

object SimpleMessageResponse
  extends SprayJsonSupport
    with DefaultJsonProtocol {

  val DefaultErrorMessage = "Failed to parse your fact"
  val DefaultErrorResponse = SimpleMessageResponse(DefaultErrorMessage)

  implicit val SimpleMessageResponseFormat: RootJsonFormat[SimpleMessageResponse] = jsonFormat1(SimpleMessageResponse.apply)
}
