package org.ciroque.animalia.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FunSpec, Matchers}

class FactApiSpec
  extends FunSpec
    with FactApi
    with ScalatestRouteTest
    with Matchers {

  describe("FactApi") {
    val path = "/animals/facts"

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
    }
  }
}
