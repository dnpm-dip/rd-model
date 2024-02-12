package de.dnpm.dip.rd.model


import java.time.LocalDate
import cats.Applicative
import de.dnpm.dip.coding.Coding
import de.dnpm.dip.model.{
  Id,
  Episode,
  ExternalId,
  Patient,
  Period,
  Reference,
  TTAN
}
import play.api.libs.json.{
  Json,
  OFormat
}


final case class Clinician
(
  name: String
)

object Clinician
{

  implicit val format: OFormat[Clinician] =
    Json.format[Clinician]
}


sealed trait GestaltMatcher
object GestaltMatcher
{
  implicit val codingSystem: Coding.System[GestaltMatcher] =
    Coding.System[GestaltMatcher]("https://www.gestaltmatcher.org/")
}


final case class RDCase
(
  id: Id[RDCase],
  externalId: Option[ExternalId[RDCase]],
  ttan: Id[TTAN],
  gestaltMatcherId: Option[ExternalId[RDCase]], 
  patient: Reference[Patient],
  status: Coding[Episode.Status.Value],
  recordedOn: Option[LocalDate],
  period: Period[LocalDate],
  diagnoses: List[Reference[RDDiagnosis]],
  referrer: Clinician,
)
extends Episode


object RDCase
{

  implicit val format: OFormat[RDCase] =
    Json.format[RDCase]
}
