package de.dnpm.dip.rd.model


import java.time.LocalDate
import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  DefaultCodeSystem
}
import de.dnpm.dip.coding.atc.ATC
import de.dnpm.dip.model.{
  CarePlan,
  Id,
  ExternalId,
  Reference,
  Patient,
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
  medication: Option[Set[Coding[ATC]]],
  supportingVariants: Option[List[Reference[Variant]]]
)
extends TherapyRecommendation
{
  val indication = None
  val priority = None
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
  supportingVariants: Option[List[Reference[Variant]]],
  studies: Option[List[ExternalId[Study]]]
)
extends StudyEnrollmentRecommendation

object RDStudyEnrollmentRecommendation
{
  implicit val format: OFormat[RDStudyEnrollmentRecommendation] =
    Json.format[RDStudyEnrollmentRecommendation]
}


final case class ClinicalManagementRecommendation
(
  id: Id[ClinicalManagementRecommendation],
  patient: Reference[Patient],
  issuedOn: LocalDate,
  `type`: Coding[ClinicalManagementRecommendation.Type.Value]
)

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
  sequencingRequested: Option[Boolean],
  therapyRecommendations: Option[List[RDTherapyRecommendation]],
  studyEnrollmentRecommendation: Option[RDStudyEnrollmentRecommendation],
  clinicalManagementRecommendation: Option[ClinicalManagementRecommendation],
  notes: Option[String]
)
extends CarePlan
{
  val indication = None
  val medicationRecommendations = None
}


object RDCarePlan
{
  implicit val format: OFormat[RDCarePlan] =
    Json.format[RDCarePlan]
}
