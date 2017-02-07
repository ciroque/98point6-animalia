package org.ciroque.animalia.controllers

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.ciroque.animalia.persistence.{DataStore, InMemoryDataStore}
import org.ciroque.animalia.services.FactService

class AnimaliaApi {

  private val factApi = new FactApi {
    override implicit val factService: FactService = new FactService {
      override implicit val dataStore: DataStore = new InMemoryDataStore {}
    }
  }

  private val queryApi = new QueryApi {}

  private val rootPath = path("") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Animalia</h1>"))
    }
  }

  val routes: Route = factApi.routes ~ queryApi.routes ~ rootPath
}
