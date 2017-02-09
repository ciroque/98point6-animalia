package org.ciroque.animalia.controllers

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import org.ciroque.animalia.models.{Fact, QueryApiNotFoundResponse}
import org.ciroque.animalia.services.QueryService
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.util.Success

trait QueryApi {
  implicit val timeout: Timeout = Timeout(3, TimeUnit.SECONDS)
  implicit val queryService: QueryService

  private val rootSegment = "animals"
  private val whichSegment = "which"
  private val howManySegment = "how-many"

  private def cleanString(s: String): String = {
    s.replaceAll("\"", "").replace("'", "")
  }

  private val whichGetRoute =
    get {
      path(rootSegment / whichSegment) {
        parameters('s, 'r, 'o) {
          (s, r, o) =>
            val query = Fact(cleanString(s), cleanString(r), cleanString(o))
            onComplete(queryService.query(query)) {
              case Success(animals: List[String]) =>
                animals match {
                  case Nil => complete(formatNotFoundResponse())
                  case _ => complete(formatAnimalListResponse(animals))
                }
            }
//          case _ => complete(formatBadRequestResponse())
        }
      }
    }

  private val howManyGetRoute =
    path(rootSegment / howManySegment) {
      get {
        complete(StatusCodes.NotImplemented, "Check back later...")
      }
    }

  val routes: Route = whichGetRoute ~ howManyGetRoute

  private def formatAnimalListResponse(animals: List[String]): HttpResponse = {
    HttpResponse(StatusCodes.OK, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, animals.toJson.toString()))
  }

  private def formatNotFoundResponse(): HttpResponse = {
    HttpResponse(StatusCodes.NotFound, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, QueryApiNotFoundResponse().toJson.toString()))
  }

  private def formatBadRequestResponse(): HttpResponse = {
    HttpResponse(StatusCodes.BadRequest, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, ""))
  }
}
