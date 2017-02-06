package org.ciroque.animalia.services

import java.util.UUID

import org.ciroque.animalia.models.{Fact, FactFailedResult, SaveFactResult}
import org.ciroque.animalia.persistence.DataStore

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait FactService {
  implicit val dataStore: DataStore

  def find(uuid: UUID): Future[Option[Fact]] = {
    dataStore.find(uuid)
  }

  def upsert(fact: Fact): Future[SaveFactResult] = {
    if(Fact.relationshipIsValid(fact.rel)) {
      dataStore.find(fact).flatMap {
        case Some(uuid: UUID) => Future.successful(SaveFactResult(uuid))
        case None => save(fact)
      }
    } else {
      Future.failed(FactFailedResult("Failed to parse your fact"))
    }
  }

  private def save(fact: Fact): Future[SaveFactResult] = {
    dataStore.store(fact).flatMap {
      case Some(uuid: UUID) => Future.successful(SaveFactResult(uuid))
    }
  }
}
