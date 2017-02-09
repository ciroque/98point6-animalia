package org.ciroque.animalia.persistence

import java.util.UUID

import org.ciroque.animalia.models.Fact
import org.ciroque.animalia.{Any, TrainingDataFormatter}
import org.scalatest.concurrent._
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSpec, Matchers}

class Neo4jDataStoreTest
  extends FunSpec
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with Matchers
    with ScalaFutures {

  var dataStore: Neo4jDataStore = _

  override def afterAll(): Unit = {
    super.afterAll()

    // TODO: Empty database
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    dataStore = Neo4jDataStore("bolt://localhost:7687", "animalia", "Password23")
//    TrainingDataFormatter.convertAnimaliaCsvToFactList().map(dataStore.store)
    import spray.json._
    TrainingDataFormatter.convertAnimaliaCsvToFactList().foreach(j => println(j.toJson.toString()))
  }

  describe("Neo4jDataStoreTest") {
    describe("store") {
      it("adds a fact") {
        val fact = Fact(Any.string(), "isa", Any.string())
        whenReady(dataStore.store(fact)) {
          uuid: UUID =>
            uuid shouldNot be(null)
        }
      }

      it("adds a fact long names") {
        val subject = Any.string(1000)
        val `object` = Any.string(1000)
        val fact = Fact(subject, "isa", `object`)
        whenReady(dataStore.store(fact)) {
          uuid: UUID =>
            uuid shouldNot be(null)
        }
      }
    }

    describe("delete") {
      it("deletes a fact") {
        val fact = Fact(Any.string(), "isa", Any.string())
        whenReady(dataStore.store(fact)) {
          uuid: UUID =>
            whenReady(dataStore.delete(uuid)) {
              deletedUuid: Option[UUID] =>
                deletedUuid.get shouldBe uuid
            }
        }
      }

      it("does not delete a non-existent fact") {
        val uuid = Any.uuid
        whenReady(dataStore.delete(uuid)) {
          deletedUuid: Option[UUID] =>
            deletedUuid shouldBe None
        }
      }
    }

    describe("find") {
      // lots of duplication in these tests, consolidate into helper functions

      it("finds a fact by UUID") {
        val factOne = Fact(Any.string(), "isa", Any.string())
        val factTwo = Fact(Any.string(), "has", Any.string())

        whenReady(dataStore.store(factOne)) {
          factOneUuid: UUID =>
            whenReady(dataStore.store(factTwo)) {
              _: UUID =>
                whenReady(dataStore.find(factOneUuid)) {
                  findResult: Option[Fact] =>
                    findResult.get shouldBe factOne
                }
            }
        }
      }

      it("does not find a fact by random UUID") {
        val factOne = Fact(Any.string(), "isa", Any.string())
        val factTwo = Fact(Any.string(), "has", Any.string())

        whenReady(dataStore.store(factOne)) {
          _: UUID =>
            whenReady(dataStore.store(factTwo)) {
              _: UUID =>
                whenReady(dataStore.find(Any.uuid)) {
                  findResult: Option[Fact] =>
                    findResult shouldBe None
                }
            }
        }
      }
    }

    describe("store find and delete") {
      it("handles multiple operations") {
        val factOne = Fact(Any.string(), "isa", Any.string())
        val factTwo = Fact(Any.string(), "has", Any.string())

        whenReady(dataStore.store(factOne)) {
          factOneUuid: UUID =>
            factOneUuid shouldNot be(null)
            whenReady(dataStore.store(factTwo)) {
              factTwoUuid: UUID =>
                factTwoUuid shouldNot be(null)
                whenReady(dataStore.find(factOneUuid)) {
                  findResult: Option[Fact] =>
                    findResult.get shouldBe factOne
                    whenReady(dataStore.delete(factOneUuid)) {
                      deletedUuid: Option[UUID] =>
                        deletedUuid.get shouldBe factOneUuid
                    }
                }
            }
        }
      }
    }

    describe("query") {
      describe("which") {
        it("which animals have legs") {
          val query = Fact("animal", "has", "leg")
          whenReady(dataStore.query(query)) {
            animals: List[String] =>
              animals shouldBe List("bear", "cormorant", "coyote", "deer", "gecko", "heron", "otter", "skunk", "spider")
          }
        }
        it("Which animals have fins") {
          val query = Fact("animal", "has", "fin")
          whenReady(dataStore.query(query)) {
            animals: List[String] =>
              animals shouldBe List("herring", "salmon")
          }
        }
        it("Which animals eat berries") {
          val query = Fact("animal", "eats", "berries")
          whenReady(dataStore.query(query)) {
            animals: List[String] =>
              animals shouldBe List("bear", "deer", "skunk")
          }
        }
        it("Which animals eat mammals") {
          val query = Fact("animal", "eats", "mammal")
          whenReady(dataStore.query(query)) {
            animals: List[String] =>
              animals shouldBe List("coyote")
          }
        }
        it("Which bears have scales") {
          val query = Fact("bear", "has", "scale")
          whenReady(dataStore.query(query)) {
            animals: List[String] =>
              animals shouldBe List()
          }
        }
        it("Which fish have scale") {
          val query = Fact("fish", "has", "scale")
          whenReady(dataStore.query(query)) {
            animals: List[String] =>
              animals shouldBe List()
          }
        }
        it("Which mammals live in the ocean") {
          val query = Fact("mammal", "lives", "ocean")
          whenReady(dataStore.query(query)) {
            animals: List[String] =>
              animals shouldBe List("otter")
          }
        }
      }
      describe("how many") {
        it("how many animals have legs") {
          val query = Fact("animal", "has", "leg")
          whenReady(dataStore.queryCount(query)) {
            count: Int =>
              count shouldBe 9
          }
        }
        it("how many animals have fins") {
          val query = Fact("animal", "has", "fin")
          whenReady(dataStore.queryCount(query)) {
            count: Int =>
              count shouldBe 2
          }
        }
        it("how many animals eat berries") {
          val query = Fact("animal", "eats", "berries")
          whenReady(dataStore.queryCount(query)) {
            count: Int =>
              count shouldBe 3
          }
        }
        it("how many animals eat mammals") {
          val query = Fact("animal", "eats", "mammal")
          whenReady(dataStore.queryCount(query)) {
            count: Int =>
              count shouldBe 1
          }
        }
        it("how many bears have scales") {
          val query = Fact("bear", "has", "scale")
          whenReady(dataStore.queryCount(query)) {
            count: Int =>
              count shouldBe 0
          }
        }
        it("how many fish have scale") {
          val query = Fact("fish", "has", "scale")
          whenReady(dataStore.queryCount(query)) {
            count: Int =>
              count shouldBe 0
          }
        }
        it("how many mammals live in the ocean") {
          val query = Fact("mammal", "lives", "ocean")
          whenReady(dataStore.queryCount(query)) {
            count: Int =>
              count shouldBe 1
          }
        }
      }
    }
  }
}
