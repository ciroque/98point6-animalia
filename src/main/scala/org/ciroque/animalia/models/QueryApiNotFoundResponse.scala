package org.ciroque.animalia.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

case class QueryApiNotFoundResponse(error: String)

object QueryApiNotFoundResponse extends SprayJsonSupport with DefaultJsonProtocol {

  // Serialization to/from JSON
  implicit val QueryApiNotFoundResponseFormat: RootJsonFormat[QueryApiNotFoundResponse] = jsonFormat1(QueryApiNotFoundResponse.apply)

  def apply(): QueryApiNotFoundResponse = {
    QueryApiNotFoundResponse("I can't answer your query.")
  }
}
