package de.dnpm.dip.hpo.impl


import java.io.InputStream
import java.time.{
  LocalDate,
  LocalTime
}
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
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


  // Extractor for HPO IDs:
  // In the ontology the node IDs are URL Strings 'http://purl.obolibrary.org/obo/HP_...'
  // so strip redundant URL part and extract only the informative part 'HP_...'
  private object HpoId
  {
    def unapply(url: String): Option[Code[HPO]] =
      Some(
        Code[HPO](
          url.substring(url.lastIndexOf("/") + 1)
            // In all human-readable representations the HPO IDs are also always of pattern 'HP:...',
            // so replace the underscore by colon for internal consistency
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

    val theVersion =
      Some(version)

    // Load only nodes of type "CLASS" for now
    val classes =
      (graph \ "nodes").as[JsArray]
        .value
        .withFilter(
          js =>
            (js \ "type").asOpt[String].exists(_ == "CLASS") &&
            (js \ "lbl").isDefined
        )

        
    val (parents,children) =
      (graph \ "edges").as[JsArray]
        .value
        .filter(
          js => (js \ "pred").as[String] == "is_a"  
        )
        .foldLeft(
          Map.empty[Code[HPO],Set[String]] -> Map.empty[Code[HPO],Set[Code[HPO]]]
        ){
          case ((parents,children),js) =>
        
            val HpoId(child) = (js \ "sub").as[String]
            val HpoId(parent) = (js \ "obj").as[String]
        
           parents.updatedWith(child)(
             _.map(_ + parent.value)
              .orElse(Some(Set(parent.value)))
           ) -> children.updatedWith(parent)(
             _.map(_ + child)
              .orElse(Some(Set(child)))
           ) 
        
        }

    val concepts =
      classes.map {
        js =>

          val id = (js \ "id").as[String]

          val HpoId(code) = id 
          
          val superClasses =
            parents.get(code)
              .filter(_.nonEmpty)

          CodeSystem.Concept[HPO](
            code = code,
            display = (js \ "lbl").as[String],
            version = theVersion,
            properties =
              Map(HPO.Type.name -> Set((js \ "type").as[String])) ++
              superClasses.map(HPO.SuperClasses.name -> _) ++
              (js \ "meta" \ "definition" \ "val").asOpt[String].map(HPO.Definition.name -> Set(_)),

            // Leave single parent undefined, as a node can have multiple super-classes (accessible via properties)
            parent = None,
            children =
              children.get(code)
                .filter(_.nonEmpty),                              
          )
      }
      .toSeq


    CodeSystem[HPO](
      uri = Coding.System[HPO].uri,
      name = "Human-Phenotype-Ontology",
      title = Some("Human Phenotype Ontology"),
      date = dateTime,
      version = theVersion,
      properties = HPO.properties,
      concepts = concepts
    )

  }

}
