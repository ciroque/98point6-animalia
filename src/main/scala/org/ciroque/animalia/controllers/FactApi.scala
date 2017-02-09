package org.ciroque.animalia.controllers

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.util.Timeout
import org.ciroque.animalia.models.{Fact, FactIdResult, SimpleMessageResponse}
import org.ciroque.animalia.services.FactService
import spray.json._

import scala.util.{Failure, Success}

trait FactApi {
  implicit val timeout: Timeout = Timeout(3, TimeUnit.SECONDS)
  implicit val factService: FactService

  private val rootSegment = "animals"
  private val factSegment = "facts"

  private val handlePostException = ExceptionHandler {
    case t: Throwable =>
      val correlationId = UUID.randomUUID().toString
      // TODO: Logging...
      complete(formatInternalServerErrorResponse(correlationId))
  }

  private val factsGetRoute =
    path(rootSegment / factSegment / JavaUUID) {
      uuid =>
        handleExceptions(handlePostException) {
          get {
            onComplete(factService.find(uuid)) {
              case Success(findFactResult: Option[Fact]) =>
                findFactResult match {
                  case Some(fact: Fact) => complete(formatOkResponse(fact.toJson))
                  case None => complete(formatNotFoundResponse())
                }
            }
          }
        }
    }

  private val factsPostRoute =
    path(rootSegment / factSegment) {
      handleExceptions(handlePostException) {
        post {
          entity(as[Fact]) {
            fact: Fact =>
              onComplete(factService.upsert(fact)) {
                case Success(factIdResult: FactIdResult) => complete(formatOkResponse(factIdResult.toJson))
                case Failure(factFailedResult: SimpleMessageResponse) => complete(formatBadRequestResponse(factFailedResult))
              }
          }
        }
      }
    }

  private val factsDeleteRoute =
    path(rootSegment / factSegment / JavaUUID) {
      uuid =>
        handleExceptions(handlePostException) {
          delete {
            onComplete(factService.delete(uuid)) {
              case Success(deleteResult: Option[FactIdResult]) =>
                deleteResult match {
                  case Some(factIdResult: FactIdResult) => complete(formatOkResponse(factIdResult.toJson))
                  case None => complete(formatNotFoundResponse())
                }
            }
          }
        }
    }

  val routes: Route = factsPostRoute ~ factsGetRoute ~ factsDeleteRoute

  private def formatOkResponse(json: JsValue): HttpResponse = {
    HttpResponse(
      StatusCodes.OK,
      Common.corsHeaders,
      HttpEntity(MediaTypes.`application/json`, json.toString())
    )
  }

  private def formatBadRequestResponse(factFailedResult: SimpleMessageResponse): HttpResponse = {
    HttpResponse(StatusCodes.BadRequest, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, factFailedResult.toJson.toString()))
  }

  private def formatNotFoundResponse(): HttpResponse = {
    HttpResponse(StatusCodes.NotFound, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, ""))
  }

  private def formatInternalServerErrorResponse(correlationId: String) = {
    HttpResponse(StatusCodes.InternalServerError, Common.corsHeaders, HttpEntity(MediaTypes.`application/json`, s"an internal error occurred. please reference '$correlationId' when contacting support"))
  }
}
