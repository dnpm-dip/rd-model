package de.dnpm.dip.rd.model


import cats.data.NonEmptyList
import de.dnpm.dip.model.Patient
import play.api.libs.json.{
  Json,
  Format
}


final case class RDPatientRecord
(
  patient: Patient,
  diagnosis: RDDiagnosis,
  `case`: RDCase,
  hpoTerms: NonEmptyList[HPOTerm],
  ngsReports: NonEmptyList[RDNGSReport],
  therapy: Option[RDTherapy]
)

object RDPatientRecord
{

  import de.dnpm.dip.util.json._

  implicit val format: Format[RDPatientRecord] =
    Json.format[RDPatientRecord]

}
