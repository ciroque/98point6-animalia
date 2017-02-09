package org.ciroque.animalia.services

import org.ciroque.animalia.models.Fact
import org.ciroque.animalia.persistence.DataStore

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

trait QueryService {

  implicit val dataStore: DataStore

  def query(query: Fact): Future[List[String]] = {
    dataStore.query(query)
  }

  def queryCount(query: Fact): Future[Int] = {
    dataStore.queryCount(query)
  }
}
