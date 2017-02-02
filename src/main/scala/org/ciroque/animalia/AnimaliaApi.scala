package org.ciroque.animalia

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.ciroque.animalia.controllers.{FactApi, QueryApi}

class AnimaliaApi {

  private val factApi = new FactApi {}

  private val queryApi = new QueryApi {}

  private val rootPath = path("") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Animalia</h1>"))
    }
  }

  val routes: Route = factApi.routes ~ queryApi.routes ~ rootPath
}
