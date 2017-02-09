package org.ciroque.animalia.controllers

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.ciroque.animalia.persistence.{DataStore, Neo4jDataStore}
import org.ciroque.animalia.services.{FactService, QueryService}

class AnimaliaApi {

  private val systemDataStore = Neo4jDataStore("bolt://localhost:7687", "neo4j", "Password23")

  private val factApi = new FactApi {
    override implicit val factService: FactService = new FactService {
      override implicit val dataStore: DataStore = systemDataStore
    }
  }

  private val queryApi = new QueryApi {
    override implicit val queryService: QueryService = new QueryService {
      override implicit val dataStore: DataStore = systemDataStore
    }
  }

  private val rootPath = path("") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Animalia</h1>"))
    }
  }

  val routes: Route = factApi.routes ~ queryApi.routes ~ rootPath
}
