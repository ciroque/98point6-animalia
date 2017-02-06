package org.ciroque.animalia.services

import org.ciroque.animalia.models.{Fact, FactFailedResult, FactSuccessResult}
import org.ciroque.animalia.persistence.InMemory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

class FactServiceTest
  extends FunSpec
  with ScalaFutures
  with Matchers {

  val service = new FactService {
    implicit val dataStore = new InMemory()
  }

  describe("FactService") {
    describe("saveFact") {
      it("saves a valid fact") {
        val fact = Fact("subject", "has", "object")
        val factResultFuture = service.upsertFact(fact)

        whenReady(factResultFuture) {
          factResult =>
            factResult.id shouldNot be(null)
        }
      }

      it("returns the same id for the same fact") {
        val fact = Fact("subject", "lives", "object")
        val firstResultFuture = service.upsertFact(fact)

        whenReady(firstResultFuture) {
          firstResult: FactSuccessResult =>
          val secondFactFuture = service.upsertFact(fact)
          whenReady(secondFactFuture) {
            secondResult: FactSuccessResult =>
            secondResult.id shouldBe firstResult.id
          }
        }
      }

      it("fails on an invalid relationship") {
        val fact = Fact("subject", "invalid", "object")
        val factResultFuture = service.upsertFact(fact)

        whenReady(factResultFuture.failed) {
          factFailedResult =>
            factFailedResult shouldBe a [FactFailedResult]
            factFailedResult.getMessage shouldBe "Failed to parse your fact"
        }
      }
    }
  }
}
