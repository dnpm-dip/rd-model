package de.dnpm.dip.orphanet.impl


import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import scala.util.Try
import scala.util.matching.Regex
import scala.xml.XML
import de.dnpm.dip.coding.{
  Code,
  Coding,
  CodeSystem
}
import de.dnpm.dip.rd.model.Orphanet



object OwlOrdoParser
{

  import scala.util.chaining._
  import Orphanet.extensions._


  // Extractor of Orpha-ID from ORDO concept URL:
  private object OrphaURL
  {
    // In ORDO, concept references/IDs occur as URLs "http://www.orpha.net/ORDO/Orphanet_..."
    // but in human-readable representations they are always given as ORPHA:..., 
    // so directly extract them accordingly
    def unapply(url: String): Option[String] =
      Option(url)
        .filterNot(_.isBlank)
        .map(
          _.substring(url.lastIndexOf("/") + 1)
           .replace("Orphanet_","ORPHA:")
        )
  }


  private class CodeExtractor(prefix: String)
  {
    def unapply(text: String): Option[String] =
      Option(text)
        .filter(_ startsWith prefix)
        .map(_ => text substring (text.lastIndexOf(":") + 1))
  }

  private val ICD10 =
    new CodeExtractor("ICD-10")

  private val ICD11 =
    new CodeExtractor("ICD-11")

  private val OrphaId =
    raw"(ORPHA:\d+)".r


  def read(in: InputStream): CodeSystem[Orphanet] = {

    val owl =
      XML.load(in)

    val rdf =
      "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

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
      (owl \ "Class")
        .collect {
          case cl if (cl \ "notation") exists (_.text pipe OrphaId.matches) =>

            val code =
              (cl \ "notation")
                .map(_.text)
                .collectFirst {
                  case OrphaId(code) => Code[Orphanet](code)
                }
                .get

            val display =
              (cl \ "label").text

            val alternativeTerms =
              Option(
                (cl \ "alternative_term").map(_.text)
              )
              .collect { 
                case terms if terms.nonEmpty =>
                  Orphanet.AlternativeTerms.name -> terms.toSet
              }

            val superClasses =
              Option(
                (cl \ "subClassOf")
                  .map(sc => (sc \ s"@{$rdf}resource").text)
                  .collect { case OrphaURL(code) => code }
                  .toSet
              )
              .collect {
                case codes if codes.nonEmpty =>
                  Orphanet.SuperClasses.name -> codes
              }

            val icd10Code =
              (cl \ "hasDbXref")
                .map(_.text)
                .collectFirst {
                  case ICD10(code) => Orphanet.ICD10Code.name -> Set(code)
                }
            
            val icd11Code =
              (cl \ "hasDbXref")
                .map(_.text)
                .collectFirst {
                  case ICD11(code) => Orphanet.ICD11Code.name -> Set(code)
                }
            
            
            CodeSystem.Concept[Orphanet](
              code = code,
              display = display,
              version = theVersion,
              properties =
                Map.empty ++
                  superClasses ++
                  icd10Code ++
                  icd11Code ++
                  alternativeTerms,
              parent = None,
              children = None // Requires a subsequent iteration below,
                              // once all concepts with their subclasses have been loaded
            )

        }
        .pipe(
          rawConcepts =>
            rawConcepts.map {
              concept =>
                val children =
                  rawConcepts
                    .collect {
                      case c if (c.superClasses contains concept.code) => c.code
                    }
                    .toList
            
                if (children.nonEmpty)
                  concept.copy(children = Some(children)) 
                else
                  concept
            }
      )
 
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
