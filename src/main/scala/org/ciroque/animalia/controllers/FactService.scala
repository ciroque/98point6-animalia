package org.ciroque.animalia.controllers

import java.util.concurrent.TimeUnit

import akka.http.javadsl.server.HttpServiceBase
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.util.Timeout

trait FactService extends HttpServiceBase {
  implicit val timeout: Timeout = Timeout(3, TimeUnit.SECONDS)

  private val rootSegment = "animals"
  private val factSegment = "facts"

  private val factsGetRoute = path(rootSegment / factSegment) {
    pathEndOrSingleSlash {
      get {
        complete(StatusCodes.NotImplemented, "Hello")
      }
    }
  }

  private val factsPostRoute = path(rootSegment / factSegment) {
    post {
      complete(StatusCodes.NotImplemented, "Check back later")
    }
  }

  val routes: Route = factsPostRoute ~ factsGetRoute
}
