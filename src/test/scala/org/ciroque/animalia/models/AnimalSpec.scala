package org.ciroque.animalia.models

import org.scalatest.{FunSpec, Matchers}

import scala.util.Random
import spray.json._

class AnimalSpec extends FunSpec with Matchers {
  describe("Animal") {
    describe("Relationship validation") {
      it("validates the 'has' relationship successfully") {
        Animal.relationshipIsValid("has") should equal(true)
      }

      it("validates the 'eats' relationship successfully") {
        Animal.relationshipIsValid("eats") should equal(true)
      }

      it("validates the 'lives' relationship successfully") {
        Animal.relationshipIsValid("lives") should equal(true)
      }

      it("validates the 'isa' relationship successfully") {
        Animal.relationshipIsValid("isa") should equal(true)
      }

      it("fails for substrings of valid relationships") {
        Animal.relationshipIsValid("eat") should equal(false)
        Animal.relationshipIsValid("live") should equal(false)
      }

      it("fails for invalid relationships") {
        Animal.relationshipIsValid("invalid") should equal(false)
      }

      it("fuzzy relationships (somewhat sketchy)") {
        for {
          _ <- 1 to 3000
        } yield {
          Animal.relationshipIsValid(Random.nextString(6)) should equal(false)
        }
      }
    }

    describe("JSON formatting") {
      it("serializes to json correctly") {
        pending
        val animal = Animal("bear", "isa", "mammal")
        val json = animal.toJson
      }
    }
  }
}
