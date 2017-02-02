package org.ciroque.animalia.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait QueryApi {
  private val rootSegment = "animals"
  private val whichSegment = "which"
  private val howManySegment = "how-many"

  private val whichGetRoute =
    path(rootSegment / whichSegment) {
      get {
        complete(StatusCodes.NotImplemented, "Check back later...")
      }
    }

  private val howManyGetRoute =
    path(rootSegment / howManySegment) {
      get {
        complete(StatusCodes.NotImplemented, "Check back later...")
      }
    }

  val routes: Route = whichGetRoute ~ howManyGetRoute
}
