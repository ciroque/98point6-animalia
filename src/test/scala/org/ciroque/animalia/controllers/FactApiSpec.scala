package org.ciroque.animalia.controllers

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.ciroque.animalia.models.{Fact, FactFailedResult, FactIdResult}
import org.ciroque.animalia.services.FactService
import org.scalatest.easymock.EasyMockSugar
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}
import org.easymock.EasyMock._

import scala.concurrent.Future

class FactApiSpec
  extends FunSpec
    with FactApi
    with ScalatestRouteTest
    with Matchers
    with EasyMockSugar
    with BeforeAndAfterEach {

  val mockFactService = mock[FactService]
  implicit val factService: FactService = mockFactService

  override def beforeEach() = {
    reset(mockFactService)
  }

  describe("FactApi") {
    val path = "/animals/facts"

    describe("Fact assertion") {
      it("returns a new id for a new fact ") {
        val fact = Fact("subject", "relationship", "object")
        val response = FactIdResult(UUID.randomUUID())

        expecting {
          mockFactService.upsert(fact).andReturn(Future(response))
        }
        whenExecuting(mockFactService) {
          Post(path, fact) ~> routes ~> check {
            status should equal(StatusCodes.OK)
            responseAs[FactIdResult] shouldBe response
          }
        }
      }

      it("returns a the appropriate message for an invalid entity") {
        val fact = Fact("subject", "relationship", "object")
        expecting {
          mockFactService.upsert(fact).andReturn(Future.failed(FactFailedResult.DefaultErrorResult))
        }
        whenExecuting(mockFactService) {
          Post(path, fact) ~> routes ~> check {
            status should equal(StatusCodes.BadRequest)
            responseAs[FactFailedResult] shouldBe FactFailedResult.DefaultErrorResult
          }
        }
      }
    }

    it("returns a 500 error when the service throws") {
      val fact = Fact("subject", "relationship", "object")
      expecting {
        mockFactService.upsert(fact).andThrow(new IllegalArgumentException("TESTING 1-2-3"))
      }
      whenExecuting(mockFactService) {
        Post(path, fact) ~> routes ~> check {
          status should equal(StatusCodes.InternalServerError)
          responseAs[String] should include("There was an internal server error.")
        }
      }
    }

    describe("handled HTTP Methods") {
      it("handles POST requests") {
        val fact = Fact("subject", "relationship", "object")
        Post(path, fact) ~> routes ~> check {
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
