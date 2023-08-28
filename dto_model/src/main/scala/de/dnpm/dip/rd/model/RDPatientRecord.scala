package de.dnpm.dip.rd.model


import de.dnpm.dip.model.{
  Patient,
}
import play.api.libs.json.Json


final case class RDPatientRecord
(
  patient: Patient,
  `case`: RDCase,
  diagnosis: RDDiagnosis,
  hpoTerms: Option[List[HPOTerm]],
  ngsReport: RDNGSReport,
  therapy: Option[RDTherapy]
)

object RDPatientRecord
{
  implicit val format =
    Json.format[RDPatientRecord]

}
