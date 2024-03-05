package de.dnpm.dip.omim.impl


import java.io.InputStream
import java.time.{
  LocalDate,
  LocalTime
}
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import scala.io.Source
import scala.util.chaining._
import scala.util.matching.Regex
import de.dnpm.dip.coding.{
  Code,
  Coding,
  CodeSystem
}
import de.dnpm.dip.rd.model.OMIM


object CSVParser
{

  private val dateRegex =
    raw"(\d{4}-\d{2}-\d{2})".r.unanchored


  def read(in: InputStream): CodeSystem[OMIM] = {

    val src = 
      Source.fromInputStream(in)

    val lines =
      src.getLines()

    val issueDate =
      lines.collectFirst { case dateRegex(date) => date }

    val concepts =
      lines
        .dropWhile(_ startsWith "#")
        .map {
          csv =>

            val values = 
              csv.split("\t")

            val code =
              Code[OMIM](values(0))

            val entryType =
              values(1)

          CodeSystem.Concept[OMIM](
            code       = code,
            display    = "-",
            version    = issueDate,
            properties = Map(OMIM.EntryType.name -> Set(entryType)),
            parent     = None,
            children   = None
          )
        }
        .toSeq

    src.close
     
    CodeSystem[OMIM](
      uri        = Coding.System[OMIM].uri,
      name       = "OMIM",
      title      = Some("OMIM - Online Mendelian Inheritance in Man"),
      date       = issueDate.map(LocalDate.parse(_,ISO_LOCAL_DATE).atTime(LocalTime.MIN)),
      version    = issueDate,
      properties = OMIM.properties,
      concepts   = concepts
    )

  }

}
