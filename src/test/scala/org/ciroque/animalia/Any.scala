package org.ciroque.animalia

import java.util.UUID

import scala.util.Random

object Any {
  def string(size: Int = 10): String = Random.nextString(size)
  def uuid: UUID = UUID.randomUUID()
}
