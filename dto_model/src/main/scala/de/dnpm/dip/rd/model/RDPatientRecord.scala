package de.dnpm.dip.rd.model



import cats.data.NonEmptyList
import de.dnpm.dip.model.{
  Patient,
  PatientRecord
}
import play.api.libs.json.{
  Json,
  JsObject,
  OFormat
}


final case class RDPatientRecord
(
  patient: Patient,
  consent: JsObject,
  episodes: NonEmptyList[RDCase],
  diagnosis: RDDiagnosis,
  hpoTerms: NonEmptyList[HPOTerm],
  ngsReports: Option[List[RDNGSReport]],
  therapy: Option[RDTherapy]
)
{
  def getNgsReports = ngsReports.getOrElse(List.empty)
}

object RDPatientRecord
{

  import de.dnpm.dip.util.json._

  implicit val format: OFormat[RDPatientRecord] =
    Json.format[RDPatientRecord]

}
