package org.ciroque.animalia.services

import org.ciroque.animalia.Any
import org.ciroque.animalia.models.Fact
import org.ciroque.animalia.persistence.DataStore
import org.easymock.EasyMock._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.easymock.EasyMockSugar
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class QueryServiceTest
  extends FunSpec
    with ScalaFutures
    with Matchers
    with EasyMockSugar
    with BeforeAndAfterEach {

  val mockDataStore: DataStore = mock[DataStore]

  val service = new QueryService {
    override implicit val dataStore: DataStore = mockDataStore
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataStore)
  }

  describe("QueryService") {
    describe("which") {
      it("passes the call through to the DataStore") {
        val expected = List("animal1", "animal2")
        val query = Fact(Any.alphanumericString(), "isa", Any.alphanumericString())
        expecting {
          mockDataStore.query(query).andReturn(Future(expected))
        }
        whenExecuting(mockDataStore) {
          whenReady(service.query(query)) {
            animals: List[String] =>
              animals shouldBe expected
          }
        }
      }
    }

    describe("how-many") {
      it("passes the call through to the DataStore") {
        val expected = 9806
        val query = Fact(Any.alphanumericString(), "isa", Any.alphanumericString())
        expecting {
          mockDataStore.queryCount(query).andReturn(Future(expected))
        }
        whenExecuting(mockDataStore) {
          whenReady(service.queryCount(query)) {
            animals: Int =>
              animals shouldBe expected
          }
        }
      }
    }
  }
}
