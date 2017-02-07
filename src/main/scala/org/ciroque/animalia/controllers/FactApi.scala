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
    path(rootSegment / factSegment) {
      get {
        complete(StatusCodes.NotImplemented, "Hello")
      }
    }

  private val factsPostRoute =
    path(rootSegment / factSegment) {
      post {
        entity(as[Fact]) {
          fact: Fact =>
            onComplete(factService.upsert(fact)) {
              case Success(factIdResult: FactIdResult) => complete(formatFactIdResult(factIdResult))
              case Failure(factFailedResult: FactFailedResult) => complete(formatFactFailedResult(factFailedResult))
            }
        }
      }
    }

  private val factsDeleteRoute =
    path(rootSegment / factSegment) {
      delete {
        complete(StatusCodes.NotImplemented, "Check back later")
      }
    }

  val routes: Route = factsPostRoute ~ factsGetRoute ~ factsDeleteRoute

  def formatFactIdResult(factIdResult: FactIdResult): HttpResponse = {
    HttpResponse(
      StatusCodes.OK,
      corsHeaders,
      HttpEntity(MediaTypes.`application/json`, factIdResult.toJson.toString())
    )
  }

  def formatFactFailedResult(factFailedResult: FactFailedResult): HttpResponse = {
    HttpResponse(StatusCodes.BadRequest, corsHeaders, HttpEntity(MediaTypes.`application/json`, factFailedResult.toJson.toString()))
  }
}
