package org.ciroque.animalia.models

import org.scalatest.{FunSpec, Matchers}

import scala.util.Random
import spray.json._

class AnimalSpec extends FunSpec with Matchers {
  describe("Animal") {
    describe("Relationship validation") {
      it("validates the 'has' relationship successfully") {
        Fact.relationshipIsValid("has") should equal(true)
      }

      it("validates the 'eats' relationship successfully") {
        Fact.relationshipIsValid("eats") should equal(true)
      }

      it("validates the 'lives' relationship successfully") {
        Fact.relationshipIsValid("lives") should equal(true)
      }

      it("validates the 'isa' relationship successfully") {
        Fact.relationshipIsValid("isa") should equal(true)
      }

      it("fails for substrings of valid relationships") {
        Fact.relationshipIsValid("eat") should equal(false)
        Fact.relationshipIsValid("live") should equal(false)
      }

      it("fails for invalid relationships") {
        Fact.relationshipIsValid("invalid") should equal(false)
      }

      it("fuzzy relationships (somewhat sketchy)") {
        for {
          _ <- 1 to 3000
        } yield {
          Fact.relationshipIsValid(Random.nextString(6)) should equal(false)
        }
      }
    }

    describe("JSON formatting") {
      it("serializes to json correctly") {
        pending
        val animal = Fact("bear", "isa", "mammal")
        val json = animal.toJson
      }
    }
  }
}
