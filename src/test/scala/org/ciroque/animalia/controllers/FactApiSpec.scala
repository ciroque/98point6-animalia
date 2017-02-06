package org.ciroque.animalia.controllers

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FunSpec, Matchers}

class FactApiSpec
  extends FunSpec
    with FactApi
    with ScalatestRouteTest
    with Matchers {

  describe("FactApi") {
    val path = "/animals/facts"

//    describe("Fact assertion") {
//      it("handles a well-formed fact") {
//        val fact = Fact("subject", "relationship", "object")
//        Post(path, fact) ~> routes ~> check {
//          status should equal(StatusCodes.OK)
//          // TODO: assert the response...
//        }
//      }
//    }

    describe("handled HTTP Methods") {
      it("handles POST requests") {
        Post(path) ~> routes ~> check {
          handled should equal(true)
        }
      }

      it("handles GET requests") {
        Get(path) ~> routes ~> check {
          handled should equal(true)
        }
      }

      it("handles DELETE requests") {
        Delete(path) ~> routes ~> check {
          handled should equal(true)
        }
      }
    }

    describe("unhandled HTTP Methods") {
      // IDEA: Check the specific reason the request was rejected...

      it("does NOT handle PUT requests") {
        Put(path) ~> routes ~> check {
          handled should equal(false)
        }
      }

      it("does NOT handle HEAD requests") {
        Head(path) ~> routes ~> check {
          handled should equal(false)
        }
      }
    }
  }
}
