package org.ciroque.animalia.persistence

import java.util.UUID

import org.ciroque.animalia.models.Fact

import scala.concurrent.Future

trait DataStore {
  def find(uuid: UUID): Future[Option[Fact]]
  def find(fact: Fact): Future[Option[UUID]]
  def store(fact: Fact): Future[Option[UUID]]
}
