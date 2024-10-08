package de.dnpm.dip.rd.model



import cats.data.NonEmptyList
import de.dnpm.dip.model.{
  History,
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
  episodesOfCare: NonEmptyList[RDEpisodeOfCare],
  diagnosis: RDDiagnosis,
  gmfcsStatus: Option[List[GMFCSStatus]],
  hospitalization: Option[Hospitalization],
  hpoTerms: NonEmptyList[HPOTerm],
  ngsReports: Option[List[RDNGSReport]],
  carePlans: Option[List[RDCarePlan]],
  therapies: Option[List[History[RDTherapy]]]
)
extends PatientRecord
{
  def getNgsReports = ngsReports.getOrElse(List.empty)
}

object RDPatientRecord
{

  import de.dnpm.dip.util.json._  // For Format[NonEmptyList[_]]

  implicit val format: OFormat[RDPatientRecord] =
    Json.format[RDPatientRecord]

}
