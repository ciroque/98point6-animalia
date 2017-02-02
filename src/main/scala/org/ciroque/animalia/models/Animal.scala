package org.ciroque.animalia.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Animal(subject: String, rel: String, `object`: String)

object Animal extends SprayJsonSupport with DefaultJsonProtocol {

  // Serialization to/from JSON
  implicit val AnimalFormat: RootJsonFormat[Animal] = jsonFormat3(Animal.apply)

  // validation around the Relationship.
  // TODO: It would be better to store the valid relationships in a datastore and load them...
  def relationshipIsValid(rel: String): Boolean = {
    List("has", "eats", "lives", "isa").contains(rel)
  }
}
