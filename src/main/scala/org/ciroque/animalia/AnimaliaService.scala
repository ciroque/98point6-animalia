package org.ciroque.animalia

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.ciroque.animalia.controllers.FactService

class AnimaliaService {

  private val factService = new FactService {}

  private val rootPath = path("") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Animalia</h1>"))
    }
  }

  val routes: Route = factService.routes ~ rootPath
}
