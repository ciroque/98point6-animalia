package org.ciroque.animalia.services

import java.util.UUID

import org.ciroque.animalia.Any
import org.ciroque.animalia.models.{Fact, FactFailedResult, FactIdResult}
import org.ciroque.animalia.persistence.InMemoryDataStore
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

/*
  Unit tests for the FactService.

  This test suite uses the Cake Pattern for dependency injections instead of a mock library.
 */
class FactServiceTest
  extends FunSpec
  with ScalaFutures
  with Matchers {

  describe("FactService") {
    describe("saveFact") {

      val service = new FactService {

        // dependency injection of the data store for tests
        implicit val dataStore = new InMemoryDataStore {

        }
      }

      it("saves a valid fact") {
        val fact = Fact("subject", "has", "object")
        val factResultFuture = service.upsert(fact)

        whenReady(factResultFuture) {
          factResult: FactIdResult =>
            factResult.id shouldNot be(null)
        }
      }

      it("returns the same id for the same fact") {
        val fact = Fact("subject", "lives", "object")
        val firstResultFuture = service.upsert(fact)

        whenReady(firstResultFuture) {
          firstResult: FactIdResult =>
          val secondFactFuture = service.upsert(fact)
          whenReady(secondFactFuture) {
            secondResult: FactIdResult =>
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
      val firstUUID = Any.uuid
      val firstFact = Fact("subject1", "has", "object1")

      val secondUUID = Any.uuid
      val secondFact = Fact("subject2", "has", "object2")

      val data = Map[UUID, Fact](
        firstUUID -> firstFact,
        secondUUID -> secondFact
      )

      val service = new FactService {

        // dependency injection of the data store for tests
        // for this group of tests the data is being injected as well
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
        val uuid = Any.uuid
        whenReady(service.find(uuid)) {
          foundFact: Option[Fact] =>
            foundFact shouldBe None
        }
      }
    }

    describe("deleteFact") {
      val firstUUID = Any.uuid
      val firstFact = Fact("subject1", "has", "object1")

      val secondUUID = Any.uuid
      val secondFact = Fact("subject2", "has", "object2")

      val data = Map[UUID, Fact](
        firstUUID -> firstFact,
        secondUUID -> secondFact
      )

      val service = new FactService {

        // dependency injection of the data store for tests
        // for this group of tests the data is being injected as well
        implicit val dataStore = new InMemoryDataStore {
          facts = data
        }
      }

      it("returns the Some(id) when the given id is found") {
        whenReady(service.delete(secondUUID)) {
          factIdResult: Option[FactIdResult] =>
            factIdResult.get.id shouldBe secondUUID
        }
      }

      it("returns None when the given id is not found") {
        whenReady(service.delete(Any.uuid)) {
          factIdResult: Option[FactIdResult] =>
            factIdResult shouldBe None
        }
      }
    }
  }
}
