package de.dnpm.dip.hpo.impl


import java.io.InputStream
import java.time.{
  LocalDate,
  LocalTime
}
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import scala.util.matching.Regex
import play.api.libs.json.{
  Json,
  JsObject,
  JsArray
}
import de.dnpm.dip.coding.{
  Code,
  Coding,
  CodeSystem
}
import de.dnpm.dip.rd.model.HPO


object JsonParser
{

  private val Date =
    raw"(\d{4}-\d{2}-\d{2})".r.unanchored

  object Id
  {

    def unapply(id: String): Option[Code[HPO]] =
      Some(
        Code[HPO](
          id.substring(id.lastIndexOf("/") + 1)
            .replace("_",":")
        )
      )
  }


  def read(in: InputStream): CodeSystem[HPO] = {

    val json =
      Json.parse(in)

    val graph =
      (json \ "graphs" \ 0).as[JsObject]

    val Date(version) =
      (graph \ "meta" \ "version").as[String]

    val dateTime = 
      (graph \ "meta" \ "basicPropertyValues")
        .as[Seq[JsObject]]
        .collectFirst {
          case js if (js \ "pred").as[String] == "http://www.w3.org/2002/07/owl#versionInfo" =>
            LocalDate
              .parse((js \ "val").as[String],ISO_LOCAL_DATE)
              .atTime(LocalTime.MIN)
        }

    // Load only nodes of type "CLASS" for now
    val classes =
      (graph \ "nodes").as[JsArray]
        .value
        .withFilter(
          js =>
            (js \ "type").asOpt[String].exists(_ == "CLASS") &&
            (js \ "lbl").isDefined
        )

    val edges =
      (graph \ "edges").as[JsArray]
        .value
        .filter(
          js => (js \ "pred").as[String] == "is_a"  
        )

    val concepts =
      classes.map {
        js =>

          val id = (js \ "id").as[String]

          val Id(code) = id 

          val superClasses =
            edges.collect {
              case edge if (edge \ "sub").as[String] == id =>
                (edge \ "obj").as[String]
            }
            .toSet

          CodeSystem.Concept[HPO](
            code = code,
            display = (js \ "lbl").as[String],
            version = Some(version),
            properties =
              Map(
                HPO.Type.name         -> Set((js \ "type").as[String]),
                HPO.Definition.name   -> (js \ "meta" \ "definition" \ "val").asOpt[String].toSet,
                HPO.SuperClasses.name -> superClasses
              ),
            // Leave single parent undefined, as a node can have multiple super-classes (accessible via properties)
            parent = None,
            children =
              Some(
                edges.collect {
                  case edge if (edge \ "obj").as[String] == id =>
                    val Id(child) = (edge \ "sub").as[String]
                    child
                }
                .toList
              )
              .filter(_.nonEmpty),
          )
      }
      .toSeq


    CodeSystem[HPO](
      uri = Coding.System[HPO].uri,
      name = "Human-Phenotype-Ontology",
      title = Some("Human Phenotype Ontology"),
      date = dateTime,
      version = Some(version),
      properties = List.empty,
      concepts = concepts
    )

  }

}

