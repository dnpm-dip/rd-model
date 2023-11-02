package de.dnpm.dip.rd.model


import java.time.LocalDate
import cats.Applicative
import de.dnpm.dip.model.{
  Id,
  ExternalId,
  Reference,
  Patient,
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

final case class RDCase
(
  id: Id[RDCase],
  externalId: Option[ExternalId[RDCase]],
  gestaltMatcherId: Option[ExternalId[RDCase]], 
//  face2geneId: Option[ExternalId[RDCase]], 
  patient: Reference[Patient],
  recordedOn: Option[LocalDate],
  referrer: Clinician,
  reason: Reference[RDDiagnosis],
)

object RDCase
{

  implicit val format: OFormat[RDCase] =
    Json.format[RDCase]
}
