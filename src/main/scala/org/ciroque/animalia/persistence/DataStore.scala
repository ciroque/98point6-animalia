package org.ciroque.animalia.persistence

import java.util.UUID

import org.ciroque.animalia.models.Fact

import scala.concurrent.Future

/*
  The data store interface.
  The Fact and Query services define the operations necessary to support the API implementation.

  This allows easy mocking in tests and the ability to create and migrate to / from various backend databases.
 */
trait DataStore {
  def delete(uuid: UUID): Future[Option[UUID]]

  def find(uuid: UUID): Future[Option[Fact]]

  def query(fact: UUID): Future[List[String]]

  def store(fact: Fact): Future[UUID]
}
