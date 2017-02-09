package org.ciroque.animalia.controllers

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{MissingQueryParamRejection, RejectionHandler, Route}
import akka.util.Timeout
import org.ciroque.animalia.models.{Fact, QueryApiNotFoundResponse, SimpleMessageResponse}
import org.ciroque.animalia.services.QueryService
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.util.{Failure, Success}

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
    get {
      path(rootSegment / howManySegment) {
        parameters('s, 'r, 'o) {
          (s, r, o) =>
            val query = Fact(cleanString(s), cleanString(r), cleanString(o))
            onComplete(queryService.queryCount(query)) {
              case Success(count: Int) =>
                count match {
                  case 0 => complete(formatNotFoundResponse())
                  case _ => complete(formatAnimalCountResponse(count))
                }
              case Failure(t) =>
                val correlationId = UUID.randomUUID().toString
                complete(formatInternalServerErrorResponse(correlationId))
            }
          //          case _ => complete(formatBadRequestResponse())
        }
      }
    }

  implicit val handleRejections: RejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case MissingQueryParamRejection(parameterName) => complete(formatBadRequestResponse(s"""missing parameter "$parameterName""""))
    }
    .result()

  val routes: Route = whichGetRoute ~ howManyGetRoute

  private def formatAnimalCountResponse(count: Int): HttpResponse = {
    HttpResponse(StatusCodes.OK, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, count.toString))
  }

  private def formatAnimalListResponse(animals: List[String]): HttpResponse = {
    HttpResponse(StatusCodes.OK, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, animals.toJson.toString()))
  }

  private def formatNotFoundResponse(): HttpResponse = {
    HttpResponse(StatusCodes.NotFound, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, QueryApiNotFoundResponse().toJson.toString()))
  }

  private def formatBadRequestResponse(message: String): HttpResponse = {
    HttpResponse(StatusCodes.BadRequest, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, SimpleMessageResponse(message).toJson.toString()))
  }

  private def formatInternalServerErrorResponse(correlationId: String) = {
    HttpResponse(StatusCodes.InternalServerError, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, s"an internal error occurred. please reference '$correlationId' when contacting support"))
  }
}
