package org.ciroque.animalia

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Main extends App {
  implicit val system = ActorSystem("98point6-animalia")
  implicit val material = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val service = new AnimaliaApi()
  Http().bindAndHandle(service.routes, "0.0.0.0", 9806)
}
