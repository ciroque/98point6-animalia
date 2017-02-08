package org.ciroque.animalia.models

import org.scalatest.{FunSpec, Matchers}

import scala.util.Random

class AnimalSpec extends FunSpec with Matchers {
  describe("Animal") {
    describe("Relationship validation") {
      it("validates the 'has' relationship successfully") {
        Fact.relationshipIsValid("has") shouldBe true
      }

      it("validates the 'eats' relationship successfully") {
        Fact.relationshipIsValid("eats") shouldBe true
      }

      it("validates the 'lives' relationship successfully") {
        Fact.relationshipIsValid("lives") shouldBe true
      }

      it("validates the 'isa' relationship successfully") {
        Fact.relationshipIsValid("isa") shouldBe true
      }

      it("fails for substrings of valid relationships") {
        Fact.relationshipIsValid("eat") shouldBe false
        Fact.relationshipIsValid("live") shouldBe false
      }

      it("fails for invalid relationships") {
        Fact.relationshipIsValid("invalid") shouldBe false
      }

      it("fuzzy relationships (somewhat sketchy)") {
        for {
          _ <- 1 to 3000
        } yield {
          Fact.relationshipIsValid(Random.nextString(6)) shouldBe false
        }
      }
    }

    //    describe("JSON formatting") {
    //      it("serializes to json correctly") {
    //        pending
    //        val animal = Fact("bear", "isa", "mammal")
    //        val json = animal.toJson
    //      }
    //    }
  }
}
