package org.ciroque.animalia.persistence

import java.util.UUID

import org.ciroque.animalia.models.Fact

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait InMemoryDataStore extends DataStore {
  implicit var facts: Map[UUID, Fact] = Map()

  override def find(uuid: UUID): Future[Option[Fact]] = {
    Future {
      facts.find { case (id: UUID, f: Fact) => id == uuid }.map(f => f._2)
    }
  }

  override def store(fact: Fact): Future[UUID] = {
    find(fact).flatMap {
      case Some(uuid: UUID) => Future(uuid)
      case None => val uuid = UUID.randomUUID()
        facts = facts + (uuid -> fact)
        Future.successful(uuid)
    }
  }

  override def delete(uuid: UUID): Future[Option[UUID]] = {
    Future {
      if (facts.keySet.contains(uuid)) {
        facts = facts - uuid
        Some(uuid)
      } else {
        None
      }
    }
  }

  private def find(fact: Fact): Future[Option[UUID]] = {
    Future {
      facts.find { case (uuid: UUID, f: Fact) => f.subject == fact.subject && f.rel == fact.rel && f.`object` == fact.`object` }.map(f => f._1)
    }
  }

  override def query(fact: Fact): Future[Nothing] = Future.failed(new NotImplementedError())

  override def queryCount(fact: Fact): Future[Int] = Future(0)
}
