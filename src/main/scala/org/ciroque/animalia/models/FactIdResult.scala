package org.ciroque.animalia.models

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

case class FactIdResult(id: UUID) {}

object FactIdResult
  extends SprayJsonSupport
    with DefaultJsonProtocol {

  implicit object UuidJsonFormat extends JsonFormat[UUID] {
    def write(x: UUID) = JsString(x toString())

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val FactIdResultFormat: RootJsonFormat[FactIdResult] = jsonFormat1(FactIdResult.apply)
}
