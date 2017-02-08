package org.ciroque.animalia.controllers

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import org.ciroque.animalia.models.{Fact, FactFailedResult, FactIdResult}
import org.ciroque.animalia.services.FactService
import spray.json._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

trait FactApi {
  implicit val timeout: Timeout = Timeout(3, TimeUnit.SECONDS)
  implicit val factService: FactService

  val corsHeaders = List(
    RawHeader("Access-Control-Allow-Origin", "*"),
    RawHeader("Access-Control-Allow-Headers", "Content-Type"),
    RawHeader("Access-Control-Allow-Methods", "GET,POST,DELETE")
  )

  private val rootSegment = "animals"
  private val factSegment = "facts"

  private val factsGetRoute =
    path(rootSegment / factSegment / JavaUUID) {
      uuid =>
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

  private val factsPostRoute =
    path(rootSegment / factSegment) {
      post {
        entity(as[Fact]) {
          fact: Fact =>
            onComplete(factService.upsert(fact)) {
              case Success(factIdResult: FactIdResult) => complete(formatOkResponse(factIdResult.toJson))
              case Failure(factFailedResult: FactFailedResult) => complete(formatBadRequestResponse(factFailedResult))
            }
        }
      }
    }

  private val factsDeleteRoute =
    path(rootSegment / factSegment / JavaUUID) {
      uuid =>
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

  val routes: Route = factsPostRoute ~ factsGetRoute ~ factsDeleteRoute

  private def formatOkResponse(json: JsValue): HttpResponse = {
    HttpResponse(
      StatusCodes.OK,
      corsHeaders,
      HttpEntity(MediaTypes.`application/json`, json.toString())
    )
  }

  private def formatBadRequestResponse(factFailedResult: FactFailedResult): HttpResponse = {
    HttpResponse(StatusCodes.BadRequest, corsHeaders, HttpEntity(MediaTypes.`application/json`, factFailedResult.toJson.toString()))
  }

  private def formatNotFoundResponse(): HttpResponse = {
    HttpResponse(StatusCodes.NotFound, corsHeaders, HttpEntity(MediaTypes.`application/json`, ""))
  }
}
