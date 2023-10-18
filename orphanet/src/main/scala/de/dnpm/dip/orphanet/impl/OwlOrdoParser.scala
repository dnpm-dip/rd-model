package de.dnpm.dip.orphanet.impl


import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import scala.util.Try
import scala.util.matching.Regex
import scala.xml.{
  XML
}
import de.dnpm.dip.coding.{
  Code,
  Coding,
  CodeSystem
}
import de.dnpm.dip.rd.model.Orphanet



object OwlOrdoParser
{

  import scala.util.chaining._


/*
  private object OrdoId
  {
    def unapply(url: String): Option[Code[Orphanet]] =
      Some(
        Code[Orphanet](
          url.substring(url.lastIndexOf("/"))
            .replace("Orphanet_","ORPHA:")
        )
      )
  }
*/

  private val icd10Ref =
    raw"ICD-10:([A-Z]{1}\d{2}.\d{1}\+?\*?)".r

  private val OrphaId =
    raw"(ORPHA:\d+)".r


  def read(in: InputStream): CodeSystem[Orphanet] = {

    val owl =
      XML.load(in)

    val dateTime =
      LocalDateTime.parse(
        (owl \ "Ontology" \ "modified").text,
        ISO_LOCAL_DATE_TIME
      )

    val theVersion =
      Some((owl \ "Ontology" \ "versionInfo").text)
   

    // This parses/extracts only Classes for which
    // the ORPHA-ID in <skos:notation>ORPHA:...</skos:notation> is defined
    val concepts =
      (owl \\ "Class")
        .collect {
          case cl if (cl \ "notation").exists(_.text pipe OrphaId.matches) =>
//          case cl if (cl \ "notation").nonEmpty =>

            val code =
              (cl \ "notation")
                .map(_.text)
                .collectFirst { 
                  case OrphaId(code) => Code[Orphanet](code)
                }
                .get

            val display =
              (cl \ "label").text

            //TODO: extract superclasses
//            val superClasses =
//              (cl \ )

            val alternativeTerms =
              Option(
                (cl \ "alternative_term").map(_.text)
              )
              .collect { 
                case terms if terms.nonEmpty =>
                  Orphanet.AlternativeTerms.name -> terms.toSet
              }

            val icd10Code =
              (cl \ "hasDbXref")
                .map(_.text)
                .collectFirst {
                  case icd10Ref(code) =>
                    Orphanet.ICD10Code.name -> Set(code)
                }
            
            
            CodeSystem.Concept[Orphanet](
              code = code,
              display = display,
              version = theVersion,
              properties =
                Map.empty ++
                  icd10Code ++
                  alternativeTerms,
              parent = None,
              children = None  // TODO
            )

        }

 
    CodeSystem[Orphanet](
      uri = Coding.System[Orphanet].uri,
      name = "ORDO",
      title = Some("Orphanet Rare Disease Ontology"),
      version = theVersion,
      date = Some(dateTime),
      properties = Orphanet.properties,
      concepts = concepts
    )

  }


}
