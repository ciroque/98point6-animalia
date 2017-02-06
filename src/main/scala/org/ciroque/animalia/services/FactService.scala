package org.ciroque.animalia.services

import java.util.UUID

import org.ciroque.animalia.models.{Fact, FactFailedResult, FactSuccessResult}
import org.ciroque.animalia.persistence.DataStore

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

trait FactService {
  implicit val dataStore: DataStore

  def upsertFact(fact: Fact): Future[FactSuccessResult] = {
    if(Fact.relationshipIsValid(fact.rel)) {
      dataStore.find(fact).flatMap {
        case Some(uuid: UUID) => Future.successful(FactSuccessResult(uuid))
        case None => saveFact(fact)
      }
    } else {
      Future.failed(FactFailedResult("Failed to parse your fact"))
    }
  }

  private def saveFact(fact: Fact): Future[FactSuccessResult] = {
    dataStore.store(fact).flatMap {
      case Some(uuid: UUID) => Future.successful(FactSuccessResult(uuid))
    }
  }
}
