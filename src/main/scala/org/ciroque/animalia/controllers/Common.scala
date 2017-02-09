package org.ciroque.animalia.controllers

import akka.http.scaladsl.model.headers.RawHeader

object Common {
  val corsHeaders = List(
    RawHeader("Access-Control-Allow-Origin", "*"),
    RawHeader("Access-Control-Allow-Headers", "Content-Type"),
    RawHeader("Access-Control-Allow-Methods", "GET,POST,DELETE")
  )
}
