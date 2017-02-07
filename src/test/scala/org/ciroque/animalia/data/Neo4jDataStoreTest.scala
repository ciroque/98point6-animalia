package org.ciroque.animalia.data

import java.util.UUID

import org.ciroque.animalia.models.Fact
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}
import org.scalatest.concurrent._

class Neo4jDataStoreTest
  extends FunSpec
    with BeforeAndAfterEach
    with Matchers
    with ScalaFutures {

  var dataStore: Neo4jDataStore = _

  override def beforeEach() {
    super.beforeEach()
    dataStore = Neo4jDataStore("bolt://localhost:7687", "animalia", "Password23")
  }

  describe("Neo4jDataStoreTest") {
    it("can connect") {
      val fact = Fact("bear", "isa", "mammal")
      whenReady(dataStore.store(fact)) {
        uuid: UUID =>
          println(uuid.toString)
      }
    }
  }
}
