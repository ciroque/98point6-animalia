package org.ciroque.animalia.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.ciroque.animalia.Any
import org.ciroque.animalia.models.Fact
import org.ciroque.animalia.services.QueryService
import org.easymock.EasyMock._
import org.scalatest.easymock.EasyMockSugar
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent.Future

class QueryApiSpec
  extends FunSpec
    with QueryApi
    with ScalatestRouteTest
    with EasyMockSugar
    with BeforeAndAfterEach
    with Matchers {

  val mockQueryService: QueryService = mock[QueryService]
  implicit val queryService: QueryService = mockQueryService

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockQueryService)
  }

  describe("QueryApi") {

    describe("which") {
      val path = "/animals/which"

      it("returns the correct JSON when animals are found") {
        var query = Fact(Any.alphanumericString(), "isa", Any.alphanumericString())
        var animals = List(Any.alphanumericString(), Any.alphanumericString(), Any.alphanumericString())
        var expected = animals.toJson.toString()
        expecting {
          mockQueryService.query(query).andReturn(Future(animals))
        }
        whenExecuting(mockQueryService) {
          Get(s"""$path?s="${query.subject}"&r='${query.rel}'&o='${query.`object`}'""") ~> routes ~> check {
            status shouldBe StatusCodes.OK
            val json = responseAs[String]
            println(json)
            json shouldBe expected
          }
        }
      }

      it("returns the correct JSON when animals are found - big string") {
        val query = Fact(Any.alphanumericString(100), "isa", Any.alphanumericString(100))
        val animals = List(Any.string(), Any.alphanumericString(), Any.string())
        val expected = animals.toJson.toString()
        expecting {
          mockQueryService.query(query).andReturn(Future(animals))
        }
        whenExecuting(mockQueryService) {
          Get(s"""$path?s="${query.subject}"&r='${query.rel}'&o='${query.`object`}'""") ~> routes ~> check {
            status shouldBe StatusCodes.OK
            val json = responseAs[String]
            json shouldBe expected
          }
        }
      }

      it("returns a 404 when no animals are found") {
        val query = Fact(Any.alphanumericString(100), "isa", Any.alphanumericString(100))
        expecting {
          mockQueryService.query(query).andReturn(Future(List()))
        }
        whenExecuting(mockQueryService) {
          Get(s"""$path?s="${query.subject}"&r='${query.rel}'&o='${query.`object`}'""") ~> routes ~> check {
            status shouldBe StatusCodes.NotFound
            responseAs[String] should include("I can't answer your query.")
          }
        }
      }

      it("returns a 400 when the request is malformed") {
        pending
        Get(s"""$path?s="BAD"&r='REQUEST'""") ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.BadRequest
          responseAs[String] should include("I can't answer your query.")
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
        pending
        Get(s"""$path?s="things"&r='rel'&o='obje'""") ~> routes ~> check {
          status shouldBe StatusCodes.BadRequest
        }
      }

      it("returns the correct JSON when animals are found") {
        val query = Fact(Any.alphanumericString(), "isa", Any.alphanumericString())
        val expected = 9806
        expecting {
          mockQueryService.queryCount(query).andReturn(Future(expected))
        }
        whenExecuting(mockQueryService) {
          Get(s"""$path?s="${query.subject}"&r='${query.rel}'&o='${query.`object`}'""") ~> routes ~> check {
            status shouldBe StatusCodes.OK
            responseAs[String] shouldBe expected.toString
          }
        }
      }

      it("returns the correct JSON when animals are found - big string") {
        val query = Fact(Any.alphanumericString(100), "isa", Any.alphanumericString(100))
        val expected = 27
        expecting {
          mockQueryService.queryCount(query).andReturn(Future(expected))
        }
        whenExecuting(mockQueryService) {
          Get(s"""$path?s="${query.subject}"&r='${query.rel}'&o='${query.`object`}'""") ~> routes ~> check {
            status shouldBe StatusCodes.OK
            val json = responseAs[String] shouldBe expected.toString
          }
        }
      }

      it("returns a 404 when no animals are found") {
        val query = Fact(Any.alphanumericString(100), "isa", Any.alphanumericString(100))
        expecting {
          mockQueryService.queryCount(query).andReturn(Future(0))
        }
        whenExecuting(mockQueryService) {
          Get(s"""$path?s="${query.subject}"&r='${query.rel}'&o='${query.`object`}'""") ~> routes ~> check {
            status shouldBe StatusCodes.NotFound
            responseAs[String] should include("I can't answer your query.")
          }
        }
      }

      it("returns a 400 when the request is malformed") {
        pending
        Get(s"""$path?s="BAD"&r='REQUEST'""") ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.BadRequest
          responseAs[String] should include("I can't answer your query.")
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
