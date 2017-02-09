package org.ciroque.animalia.controllers

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.ciroque.animalia.Any
import org.ciroque.animalia.models.{Fact, FactFailedResult, FactIdResult}
import org.ciroque.animalia.services.FactService
import org.easymock.EasyMock._
import org.scalatest.easymock.EasyMockSugar
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

import scala.concurrent.Future

/*
    Unit tests for the FactAPI.

    This test suite uses EasyMock to handle the dependency injection for the Fact Service.

    Point Of Discussion. Libraries vs Hand-crafted mocks...
 */
class FactApiSpec
  extends FunSpec
    with FactApi
    with ScalatestRouteTest
    with Matchers
    with EasyMockSugar
    with BeforeAndAfterEach {

  val mockFactService: FactService = mock[FactService]
  implicit val factService: FactService = mockFactService

  override def beforeEach() = {
    reset(mockFactService)
  }

  describe("FactApi") {
    val path = "/animals/facts"

    describe("Fact assertion") {
      it("returns a new id for a new fact ") {
        val fact = Fact("subject", "relationship", "object")
        val response = FactIdResult(Any.uuid)

        expecting {
          mockFactService.upsert(fact).andReturn(Future(response))
        }
        whenExecuting(mockFactService) {
          Post(path, fact) ~> routes ~> check {
            status shouldBe StatusCodes.OK
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
            status shouldBe StatusCodes.BadRequest
            responseAs[FactFailedResult] shouldBe FactFailedResult.DefaultErrorResult
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
            status shouldBe StatusCodes.InternalServerError
            responseAs[String] should include("an internal error occurred. please reference '")
          }
        }
      }
    }

    describe("Fact Management") {
      describe("delete") {
        it("returns the uuid of a deleted fact on success") {
          val response = FactIdResult(Any.uuid)
          expecting {
            mockFactService.delete(response.id).andReturn(Future(Some(response)))
          }
          whenExecuting(mockFactService) {
            Delete(s"$path/${response.id.toString}") ~> routes ~> check {
              status shouldBe StatusCodes.OK
              responseAs[FactIdResult] shouldBe response
            }
          }
        }

        it("returns 404 with no body for a non-existent fact") {
          val response = FactIdResult(Any.uuid)
          expecting {
            mockFactService.delete(response.id).andReturn(Future(None))
          }
          whenExecuting(mockFactService) {
            Delete(s"$path/${response.id.toString}") ~> routes ~> check {
              status shouldBe StatusCodes.NotFound
              responseAs[String] shouldBe ""
            }
          }
        }
      }

      describe("get") {
        it("returns a fact when found") {
          val uuid = UUID.randomUUID()
          val fact = Fact(Any.string(), "isa", Any.string())
          expecting {
            mockFactService.find(uuid).andReturn(Future(Some(fact)))
          }
          whenExecuting(mockFactService) {
            Get(s"$path/${uuid.toString}") ~> routes ~> check {
              status shouldBe StatusCodes.OK
              responseAs[Fact] shouldBe fact
            }
          }
        }

        it("returns a 404 when not found") {
          val uuid = UUID.randomUUID()
          expecting {
            mockFactService.find(uuid).andReturn(Future(None))
          }
          whenExecuting(mockFactService) {
            Get(s"$path/${uuid.toString}") ~> routes ~> check {
              status shouldBe StatusCodes.NotFound
              responseAs[String] shouldBe ""
            }
          }
        }
      }
    }

    describe("handled HTTP Methods") {
      it("handles POST requests") {
        val fact = Fact("subject", "relationship", "object")
        Post(path, fact) ~> routes ~> check {
          handled shouldBe true
        }
      }

      it("handles GET requests") {
        Get(s"$path/${Any.uuid.toString}") ~> routes ~> check {
          handled shouldBe true
        }
      }

      it("handles DELETE requests") {
        Delete(s"$path/${Any.uuid}") ~> routes ~> check {
          handled shouldBe true
        }
      }
    }

    describe("unhandled HTTP Methods") {
      // IDEA: Check the specific reason the request was rejected...

      it("does NOT handle PUT requests") {
        Put(path) ~> routes ~> check {
          handled shouldBe false
        }
      }

      it("does NOT handle DELETE request with no UUID") {
        Delete(path) ~> routes ~> check {
          handled shouldBe false
        }
      }

      it("does NOT handle HEAD requests") {
        Head(path) ~> routes ~> check {
          handled shouldBe false
        }
      }
    }
  }
}
