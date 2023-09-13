package de.dnpm.dip.rd.model


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
  hpoTerms: Option[List[HPOTerm]],
  ngsReport: RDNGSReport,
  therapy: Option[RDTherapy]
)

object RDPatientRecord
{

  implicit val format: Format[RDPatientRecord] =
    Json.format[RDPatientRecord]

}
