package org.ciroque.animalia.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FunSpec, Matchers}

class QueryApiSpec
  extends FunSpec
    with QueryApi
    with ScalatestRouteTest
    with Matchers {

  describe("QueryApi") {

    describe("which") {
      val path = "/animals/which"

      it("handles GET requests") {
        Get(path) ~> routes ~> check {
          status shouldBe StatusCodes.NotImplemented
        }
      }

      describe("unhandled HTTP Methods") {
        // IDEA: Check the specific reason the request was rejected...

        it("does NOT handle POST requests") {
          Post(path + "/") ~> routes ~> check {
            handled shouldBe false
          }
        }

        it("does NOT handle PUT requests") {
          Put(path) ~> routes ~> check {
            handled shouldBe false
          }
        }

        it("does NOT handle DELETE requests") {
          Delete(path) ~> routes ~> check {
            handled shouldBe false
          }
        }
      }
    }

    describe("how-many") {
      val path = "/animals/how-many"

      it("handles GET requests") {
        Get(path) ~> routes ~> check {
          status shouldBe StatusCodes.NotImplemented
        }
      }

      describe("unhandled HTTP Methods") {
        // IDEA: Check the specific reason the request was rejected...

        it("does NOT handle POST requests") {
          Post(path) ~> routes ~> check {
            handled shouldBe false
          }
        }

        it("does NOT handle PUT requests") {
          Put(path) ~> routes ~> check {
            handled shouldBe false
          }
        }

        it("does NOT handle DELETE requests") {
          Delete(path) ~> routes ~> check {
            handled shouldBe false
          }
        }
      }
    }
  }
}
