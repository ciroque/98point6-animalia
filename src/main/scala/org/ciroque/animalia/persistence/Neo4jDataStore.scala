package org.ciroque.animalia.persistence

import java.util.UUID

import org.ciroque.animalia.models.Fact
import org.neo4j.driver.v1._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Neo4jDataStore extends DataStore {
  implicit val neo4jDriver: Driver

  override def delete(uuid: UUID): Future[Option[UUID]] = {
    val result = withSession {
      s"MATCH ()-[r { uuid: '${uuid.toString}' }]-() DELETE r;"
    }

    if (result.summary().counters().relationshipsDeleted() == 1) {
      Future(Some(uuid))
    } else {
      Future(None)
    }
  }

  override def find(uuid: UUID): Future[Option[Fact]] = {
    val result = withSession {
      s"MATCH (s: Fact)-[r { uuid: '${uuid.toString}' }]-(o: Fact) RETURN s.name as subject, type(r) as relationship, o.name as object;"
    }

    if (result.hasNext) {
      val record = result.next()
      Future(Some(Fact(record.get("subject").asString(), record.get("relationship").asString(), record.get("object").asString())))
    } else {
      Future(None)
    }
  }

  /*
      TODO: Read, Learn, Implement: https://neo4j.com/blog/cypher-union-query-using-collect-clause/
   */
  override def query(fact: Fact): Future[List[String]] = {
    val result = withSession {
      s"""
        MATCH (subject: Fact { name: '${fact.subject}'})<-[:${fact.rel}]->(object: Fact { name: '${fact.`object`}'})
        RETURN DISTINCT subject.name as animals
        ORDER BY animals

        UNION

        MATCH (subject { name: '${fact.subject}' })
        MATCH (object { name: '${fact.`object`}' })
        MATCH (subject)<-[:isa]->(animal)<-[r:${fact.rel}]->(object)
        MATCH (animal)<-[:isa]->({ name: 'animal' })
        RETURN DISTINCT animal.name as animals
        ORDER BY animals

        UNION

        MATCH ()<-[:has]->(animal: Fact)-[:isa]->({name: 'animal'})
        MATCH (subject: Fact { name: '${fact.subject}'})
        MATCH (object: Fact { name: '${fact.`object`}'})
        MATCH (thing)-[:isa]->(object)
        MATCH (subject)-[:isa]-(animal)-[:${fact.rel}]->(thing)
        RETURN DISTINCT animal.name as animals
        ORDER BY animals
      ;"""
    }

    Future {
      result
        .list()
        .toArray()
        .map { case r: Record => r.get("animals").asString() }
        .toList
    }
  }

  override def queryCount(fact: Fact): Future[Int] = {
    println(s"Neo4jDataStore::queryCount($fact)")
    val result = withSession {
      s"""
        MATCH (subject: Fact { name: '${fact.subject}'})<-[:${fact.rel}]->(object: Fact { name: '${fact.`object`}'})
        RETURN COUNT(DISTINCT subject.name) as count

        UNION

        MATCH (subject { name: '${fact.subject}' })
        MATCH (object { name: '${fact.`object`}' })
        MATCH (subject)<-[:isa]->(animal)<-[r:${fact.rel}]->(object)
        MATCH (animal)<-[:isa]->({ name: 'animal' })
        RETURN COUNT(DISTINCT animal.name) as count

        UNION

        MATCH ()<-[:has]->(animal: Fact)-[:isa]->({name: 'animal'})
        MATCH (subject: Fact { name: '${fact.subject}'})
        MATCH (object: Fact { name: '${fact.`object`}'})
        MATCH (thing)-[:isa]->(object)
        MATCH (subject)-[:isa]-(animal)-[:${fact.rel}]->(thing)
        RETURN COUNT(DISTINCT animal.name) as count
      ;"""
    }

    val count = result
      .list()
      .toArray()
      .map { case r: Record => r.get("count").asInt() }
      .sum

    Future(count)
  }

  /*
    Using the MERGE feature of Neo4j greatly reduces the complexity of the upsert functionality required in the spec.
    Merge automatically looks for existing Nodes and uses them, or creates the Node if it does not exist.
    The ON CREATE clause allows us to specify what happens when the Relationship is created.
    In this case we provide a UUID, which will then be returned after the Relationship is created.
    In the case that the Relationship has already been created the existing uuid property is returned.

    http://graphaware.com/neo4j/2014/07/31/cypher-merge-explained.html
   */
  override def store(fact: Fact): Future[UUID] = {
    val result = withSession {
      val newUUID: UUID = UUID.randomUUID()
      s"""MERGE (s:Fact { name: "${fact.subject}" })
         | MERGE (o:Fact { name:"${fact.`object`}" })
         | MERGE (s)-[r:${fact.rel.toLowerCase()}]-(o)
         | ON CREATE SET r.uuid = "${newUUID.toString}"
         | RETURN r.uuid AS UUID;""".stripMargin
    }

    val single = result.single()
    val uuid = UUID.fromString(single.get("UUID").asString())

    Future(uuid)
  }

  /*
  Session management
   */
  private def withSession(statement: String): StatementResult = {
    val session = neo4jDriver.session()
    try {
      println(statement)
      session.run(statement)
    } finally {
      session.close()
    }
  }
}

object Neo4jDataStore {
  def apply(uri: String, username: String, password: String): Neo4jDataStore = {
    new Neo4jDataStore {
      implicit val neo4jDriver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))
    }
  }
}
