package org.ciroque.animalia.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

case class FactFailedResult(message: String) extends Throwable(message) { }

object FactFailedResult
  extends SprayJsonSupport
    with DefaultJsonProtocol {

  val DefaultErrorMessage = "Failed to parse your fact"
  val DefaultErrorResult = FactFailedResult(DefaultErrorMessage)

  implicit val FactFailedResultFormat: RootJsonFormat[FactFailedResult] = jsonFormat1(FactFailedResult.apply)
}
