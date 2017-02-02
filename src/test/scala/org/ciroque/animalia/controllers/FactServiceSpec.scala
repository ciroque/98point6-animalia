package org.ciroque.animalia.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FunSpec, Matchers}

class FactServiceSpec
  extends FunSpec
  with FactService
    with ScalatestRouteTest
    with Matchers {

  describe("FactService") {
    val path = "/animals/facts"

    it("handles POST requests") {
      Post(path) ~> routes ~> check {
        status should equal(StatusCodes.NotImplemented)
      }
    }

    it("handles GET requests") {
      Get(path) ~> routes ~> check {
        status should equal(StatusCodes.NotImplemented)
      }
    }
  }
}
