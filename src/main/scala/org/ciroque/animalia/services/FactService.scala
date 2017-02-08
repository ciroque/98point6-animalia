package org.ciroque.animalia.services

import java.util.UUID

import org.ciroque.animalia.models.{Fact, FactFailedResult, FactIdResult}
import org.ciroque.animalia.persistence.DataStore

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait FactService {
  implicit val dataStore: DataStore

  def delete(uuid: UUID): Future[Option[FactIdResult]] = {
    dataStore.delete(uuid).flatMap {
      case Some(uuid: UUID) => Future.successful(Some(FactIdResult(uuid)))
      case None => Future.successful(None)
    }
  }

  def find(uuid: UUID): Future[Option[Fact]] = {
    dataStore.find(uuid)
  }

  def upsert(fact: Fact): Future[FactIdResult] = {
    if (Fact.relationshipIsValid(fact.rel)) {
      dataStore.store(fact).map { uuid: UUID => FactIdResult(uuid) }
    } else {
      Future.failed(FactFailedResult.DefaultErrorResult)
    }
  }
}
