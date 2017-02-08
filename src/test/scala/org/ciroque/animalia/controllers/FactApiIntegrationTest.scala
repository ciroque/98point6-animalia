package org.ciroque.animalia.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.ciroque.animalia.TrainingDataFormatter
import org.ciroque.animalia.data.Neo4jDataStore
import org.ciroque.animalia.persistence.DataStore
import org.ciroque.animalia.services.FactService
import org.scalatest.{FunSpec, Matchers}

class FactApiIntegrationTest
  extends FunSpec
    with FactApi
    with Matchers
    with ScalatestRouteTest {

  implicit val factService: FactService = new FactService {
    override implicit val dataStore: DataStore = Neo4jDataStore("bolt://localhost:7687", "neo4j", "Password23")
  }
  val path = "/animals/facts"

  describe("animalia data import") {
    it("imports the data from the provided CSV") {
      pending
      val facts = TrainingDataFormatter.convertAnimaliaCsvToFactList()
      facts.map {
        fact =>
          println(fact)
          Post(path, fact) ~> routes ~> check {
            status shouldBe StatusCodes.OK
          }
      }
    }
  }
}
