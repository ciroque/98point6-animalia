package org.ciroque.animalia.data

import java.util.UUID

import org.ciroque.animalia.models.Fact
import org.ciroque.animalia.persistence.DataStore
import org.neo4j.driver.v1._

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

trait Neo4jDataStore extends DataStore {
  implicit val neo4jDriver: Driver

  override def find(uuid: UUID): Future[Option[Fact]] = {
    val result = withSession {
      s"MATCH (s: Subject)-[r { uuid: '${uuid.toString}' }]-(o: Object) RETURN s.name as subject, type(r) as relationship, o.name as object;"
    }

    if(result.hasNext) {
      val record = result.next()
      Future(Some(Fact(record.get("subject").asString(), record.get("relationship").asString(), record.get("object").asString())))
    } else {
      Future(None)
    }
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
      s"""MERGE (s:Subject { name: "${fact.subject}" })
         | MERGE (o:Object { name:"${fact.`object`}" })
         | MERGE (s)-[r:${fact.rel.toLowerCase()}]-(o)
         | ON CREATE SET r.uuid = "${newUUID.toString}"
         | RETURN r.uuid AS UUID;""".stripMargin
    }

    val single = result.single()
    val uuid = UUID.fromString(single.get("UUID").asString())

    Future(uuid)
  }

  override def delete(uuid: UUID): Future[Option[UUID]] = {
    val result = withSession {
      s"MATCH ()-[r { uuid: '${uuid.toString}' }]-() DELETE r;"
    }

    if(result.summary().counters().relationshipsDeleted() == 1) {
      Future(Some(uuid))
    } else {
      Future(None)
    }
  }

  /*
  Session management
   */
  private def withSession(statement: String): StatementResult = {
    val session = neo4jDriver.session()
    try {
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
