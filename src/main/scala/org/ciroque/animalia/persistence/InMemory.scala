package org.ciroque.animalia.persistence
import java.util.UUID

import org.ciroque.animalia.models.Fact

import scala.concurrent.Future

class InMemory extends DataStore {
  private var facts: Map[UUID, Fact] = Map()

  override def find(fact: Fact): Future[Option[UUID]] = {
    val found = facts.find { case (uuid: UUID, f: Fact) => f.subject == fact.subject && f.rel == fact.rel && f.`object` == fact.`object` }
    found match {
      case Some((uuid: UUID, _: Fact)) => Future.successful(Some(uuid))
      case _ => Future.successful(None)
    }
  }

  override def store(fact: Fact): Future[Option[UUID]] = {
    val uuid = UUID.randomUUID()
    facts = facts + (uuid -> fact)
    Future.successful(Some(uuid))
  }
}
