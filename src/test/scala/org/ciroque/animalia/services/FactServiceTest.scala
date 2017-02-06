package org.ciroque.animalia.services

import java.util.UUID

import org.ciroque.animalia.models.{Fact, FactFailedResult, SaveFactResult}
import org.ciroque.animalia.persistence.InMemoryDataStore
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

class FactServiceTest
  extends FunSpec
  with ScalaFutures
  with Matchers {

  describe("FactService") {
    describe("saveFact") {
      val service = new FactService {
        implicit val dataStore = new InMemoryDataStore {

        }
      }

      it("saves a valid fact") {
        val fact = Fact("subject", "has", "object")
        val factResultFuture = service.upsert(fact)

        whenReady(factResultFuture) {
          factResult: SaveFactResult =>
            factResult.id shouldNot be(null)
        }
      }

      it("returns the same id for the same fact") {
        val fact = Fact("subject", "lives", "object")
        val firstResultFuture = service.upsert(fact)

        whenReady(firstResultFuture) {
          firstResult: SaveFactResult =>
          val secondFactFuture = service.upsert(fact)
          whenReady(secondFactFuture) {
            secondResult: SaveFactResult =>
            secondResult.id shouldBe firstResult.id
          }
        }
      }

      it("fails on an invalid relationship") {
        val fact = Fact("subject", "invalid", "object")
        val factResultFuture = service.upsert(fact)

        whenReady(factResultFuture.failed) {
          factFailedResult =>
            factFailedResult shouldBe a [FactFailedResult]
            factFailedResult.getMessage shouldBe "Failed to parse your fact"
        }
      }
    }

    describe("getFact") {
      val firstUUID = UUID.randomUUID()
      val firstFact = Fact("subject1", "has", "object1")

      val secondUUID = UUID.randomUUID()
      val secondFact = Fact("subject2", "has", "object2")

      val data = Map[UUID, Fact](
        firstUUID -> firstFact,
        secondUUID -> secondFact
      )

      val service = new FactService {
        implicit val dataStore = new InMemoryDataStore {
          facts = data
        }
      }

      it("finds a fact by id") {
        whenReady(service.find(firstUUID)) {
          foundFact: Option[Fact] =>
            foundFact.get shouldBe firstFact
        }
      }

      it("does not find a fact by id") {
        val uuid = UUID.randomUUID()
        whenReady(service.find(uuid)) {
          foundFact: Option[Fact] =>
            foundFact shouldBe None
        }
      }
    }
  }
}
