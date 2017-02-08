  package org.ciroque.animalia

import org.ciroque.animalia.models.Fact

import scala.io.Source

object TrainingDataFormatter {

  case class LineFact(concept: String, has: List[String], eats: List[String], lives: List[String], isa: List[String])

  object LineFact {
    def fromCsvLine(line: String): LineFact = {
      val columns = line.split(",")
      LineFact(
        columns.apply(0),
        columns.apply(1).split(":").toList,
        columns.apply(2).split(":").toList,
        columns.apply(3).split(":").toList,
        columns.apply(4).split(":").toList
      )
    }
  }

  private def processLineFact(lineFact: LineFact): List[Fact] = {
    val hasFacts = lineFact.has.filter(s => s != "").map { h: String => Fact(lineFact.concept, "has", h) }
    val eatsFacts = lineFact.eats.filter(s => s != "").map { h: String => Fact(lineFact.concept, "eats", h) }
    val livesFacts = lineFact.lives.filter(s => s != "").map { h: String => Fact(lineFact.concept, "lives", h) }
    val isaFacts = lineFact.isa.filter(s => s != "").map { h: String => Fact(lineFact.concept, "isa", h) }
    hasFacts ::: eatsFacts ::: livesFacts ::: isaFacts
  }

  private def processLine(line: String): List[Fact] = {
    val lineFact = LineFact.fromCsvLine(line)
    processLineFact(lineFact)
  }

  def convertAnimaliaCsvToFactList(): List[Fact] = {
    val resource = getClass.getResource("/animalia_data.csv")
    val source = Source.fromURL(resource)
    val things = for {
      line <- source.getLines.drop(1)
    } yield {
      processLine(line)
    }
    things.flatten.toList
  }
}
