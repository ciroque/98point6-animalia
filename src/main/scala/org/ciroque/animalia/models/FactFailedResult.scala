package org.ciroque.animalia.models

case class FactFailedResult(message: String) extends Throwable(message) { }

object FactFailedResult { }
