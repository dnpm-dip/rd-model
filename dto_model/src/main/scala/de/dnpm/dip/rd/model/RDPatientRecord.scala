package de.dnpm.dip.rd.model



import cats.data.NonEmptyList
import de.dnpm.dip.model.{
  FollowUp,
  History,
  Patient,
  PatientRecord
}
import play.api.libs.json.{
  Json,
  OFormat
}


final case class RDPatientRecord
(
  patient: Patient,
  episodesOfCare: NonEmptyList[RDEpisodeOfCare],
  diagnoses: NonEmptyList[RDDiagnosis],
  gmfcsStatus: Option[List[GMFCSStatus]],
  hospitalization: Option[Hospitalization],
  hpoTerms: NonEmptyList[HPOTerm],
  ngsReports: Option[List[RDNGSReport]],
  carePlans: Option[List[RDCarePlan]],
  followUps: Option[List[FollowUp]],
  therapies: Option[List[History[RDTherapy]]]
)
extends PatientRecord
{
  override val systemicTherapies = None

  def getNgsReports = ngsReports.getOrElse(List.empty)
}

object RDPatientRecord
{

  import de.dnpm.dip.util.json.{ 
    readsNel,
    writesNel
  }

  implicit val format: OFormat[RDPatientRecord] =
    Json.format[RDPatientRecord]

}
