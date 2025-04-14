package de.dnpm.dip.rd.model


import java.time.LocalDate
import cats.data.NonEmptyList
import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  DefaultCodeSystem
}
import de.dnpm.dip.coding.atc.ATC
import de.dnpm.dip.model.{
  CarePlan,
  Commentable,
  Id,
  Reference,
  ExternalReference,
  GeneAlterationReference,
  Patient,
  Recommendation,
  TherapyRecommendation,
  Study,
  StudyEnrollmentRecommendation
}
import play.api.libs.json.{
  Json,
  OFormat
}


final case class RDTherapyRecommendation
(
  id: Id[RDTherapyRecommendation],
  patient: Reference[Patient],
  issuedOn: LocalDate,
  category: Coding[RDTherapy.Category.Value],
  `type`: Coding[RDTherapy.Type.Value],
  medication: Option[Set[Coding[ATC]]],
  supportingVariants: Option[List[GeneAlterationReference[Variant]]]
)
extends TherapyRecommendation
{
  val reason = None
  val priority = Coding(Recommendation.Priority.One) // Irrelevant, so just set a constant
}

object RDTherapyRecommendation
{
  implicit val format: OFormat[RDTherapyRecommendation] =
    Json.format[RDTherapyRecommendation]
}


final case class RDStudyEnrollmentRecommendation
(
  id: Id[RDStudyEnrollmentRecommendation],
  patient: Reference[Patient],
  issuedOn: LocalDate,
  supportingVariants: Option[List[GeneAlterationReference[Variant]]],
  study: NonEmptyList[ExternalReference[Study,Study.Registries]]
)
extends StudyEnrollmentRecommendation

object RDStudyEnrollmentRecommendation
{

  import de.dnpm.dip.util.json.{
    readsNel,
    writesNel
  }

  implicit val format: OFormat[RDStudyEnrollmentRecommendation] =
    Json.format[RDStudyEnrollmentRecommendation]
}


final case class ClinicalManagementRecommendation
(
  id: Id[ClinicalManagementRecommendation],
  patient: Reference[Patient],
  issuedOn: LocalDate,
  `type`: Coding[ClinicalManagementRecommendation.Type.Value],
  notes: Option[List[String]]
)
extends Commentable

object ClinicalManagementRecommendation
{

  object Type
  extends CodedEnum("dnpm-dip/rd/clinical-management/type")
  with DefaultCodeSystem
  {
    val DiseaseSpecificAmbulatoryCare = Value("disease-specific-ambulatory-care")
    val UniversityAmbulatoryCare      = Value("university-ambulatory-care")
    val OtherAmbulatoryCare           = Value("other-ambulatory-care")
    val LocalCRD                      = Value("local-crd")
    val OtherCRD                      = Value("other-crd")
    val GP                            = Value("gp")
    val Specialist                    = Value("specialist")
  
    override val display =
      Map(
        DiseaseSpecificAmbulatoryCare -> "Indikatorerkrankungsspezifische Ambulanz",
        UniversityAmbulatoryCare      -> "Andere Hochschulambulanz",
        OtherAmbulatoryCare           -> "Andere Ambulanz", 
        LocalCRD                      -> "Eigenes ZSE", 
        OtherCRD                      -> "Anderes ZSE", 
        GP                            -> "Hausarzt", 
        Specialist                    -> "Niedergelassener Facharzt" 
      )
  }


  implicit val format: OFormat[ClinicalManagementRecommendation] =
    Json.format[ClinicalManagementRecommendation]
}



final case class RDCarePlan
(
  id: Id[RDCarePlan],
  patient: Reference[Patient],
  issuedOn: LocalDate,
  statusReason: Option[Coding[RDCarePlan.StatusReason.Value]],
  geneticCounselingRecommended: Option[Boolean],
  reevaluationRecommended: Option[Boolean],
  therapyRecommendations: Option[List[RDTherapyRecommendation]],
  studyEnrollmentRecommendations: Option[List[RDStudyEnrollmentRecommendation]],
  clinicalManagementRecommendation: Option[ClinicalManagementRecommendation],
  notes: Option[List[String]]
)
extends CarePlan
{
  type StatusReason = RDCarePlan.StatusReason.type

  override val reason = None
  override val medicationRecommendations = None
}


object RDCarePlan
{

  object StatusReason
  extends CodedEnum("dnpm-dip/rd/careplan/status-reason")
  with CarePlan.NonInclusionReason
  with DefaultCodeSystem
  {
    override val display = defaultDisplay
  }


  implicit val format: OFormat[RDCarePlan] =
    Json.format[RDCarePlan]
}
