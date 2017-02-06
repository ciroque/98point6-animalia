package org.ciroque.animalia.persistence
import java.util.UUID

import org.ciroque.animalia.models.Fact

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

trait InMemoryDataStore extends DataStore {
  implicit var facts: Map[UUID, Fact] = Map()

  override def find(fact: Fact): Future[Option[UUID]] = {
    Future {
      facts.find { case (uuid: UUID, f: Fact) => f.subject == fact.subject && f.rel == fact.rel && f.`object` == fact.`object` }.map(f => f._1)
    }
  }

  override def find(uuid: UUID): Future[Option[Fact]] = {
    Future {
      facts.find { case (id: UUID, f: Fact) => id == uuid }.map( f => f._2)
    }
  }

  override def store(fact: Fact): Future[Option[UUID]] = {
    val uuid = UUID.randomUUID()
    facts = facts + (uuid -> fact)
    Future.successful(Some(uuid))
  }
}
