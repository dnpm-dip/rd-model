package de.dnpm.dip.rd.model.json


import json.{
  Json,
  Schema
}
import com.github.andyglow.jsonschema.CatsSupport._ // For correct handling of NonEmptyList in Schema derivation
import de.dnpm.dip.coding.Coding
import de.dnpm.dip.model.json.BaseSchemas
import de.dnpm.dip.rd.model._


trait Schemas extends BaseSchemas
{

  implicit val diagnosisSystemsCoding: Schema[Coding[RDDiagnosis.Systems]] =
    coproductCodingSchema[RDDiagnosis.Systems]


  implicit val diagnosisSchema: Schema[RDDiagnosis] =
    Json.schema[RDDiagnosis]
      .toDefinition("Diagnosis")


  implicit val gmfcsSchema: Schema[GMFCSStatus] =
    Json.schema[GMFCSStatus]
      .toDefinition("GMFCS-Status")


  implicit val hospitalizationSchema: Schema[Hospitalization] =
    Json.schema[Hospitalization]
      .toDefinition("Hospitalization")


  implicit val episodeSchema: Schema[RDEpisodeOfCare] =
    Json.schema[RDEpisodeOfCare]
      .toDefinition("EpisodeOfCare")


  implicit val hpoTermStatusSchema: Schema[HPOTerm.Status] =
    Json.schema[HPOTerm.Status]
      .toDefinition("HPOTerm-Status")

  implicit val hpoTermSchema: Schema[HPOTerm] =
    Json.schema[HPOTerm]
      .toDefinition("HPOTerm")

 
  implicit val smallVariantSchema: Schema[SmallVariant] =
    Json.schema[SmallVariant]
      .toDefinition("SmallVariant")


  implicit val structuralVariantSchema: Schema[StructuralVariant] =
    Json.schema[StructuralVariant]
      .toDefinition("StructuralVariant")


  implicit val copyNumberVariantSchema: Schema[CopyNumberVariant] =
    Json.schema[CopyNumberVariant]
      .toDefinition("CopyNumberVariant")


  implicit val ngsReportSchema: Schema[RDNGSReport] =
    Json.schema[RDNGSReport]
      .toDefinition("NGSReport")


  implicit val therapyRecommendationSchema: Schema[RDTherapyRecommendation] =
    Json.schema[RDTherapyRecommendation]
      .toDefinition("TherapyRecommendation")


  implicit val studyEnrollmentRecommendationSchema: Schema[RDStudyEnrollmentRecommendation] =
    Json.schema[RDStudyEnrollmentRecommendation]
      .toDefinition("StudyEnrollmentRecommendation")


  implicit val clinicalManagementRecommendationSchema: Schema[ClinicalManagementRecommendation] =
    Json.schema[ClinicalManagementRecommendation]
      .toDefinition("ClinicalManagementRecommendation")


  implicit val carePlanSchema: Schema[RDCarePlan] =
    Json.schema[RDCarePlan]
      .toDefinition("CarePlan")


  implicit val therapySchema: Schema[RDTherapy] =
    Json.schema[RDTherapy]
      .toDefinition("Therapy")


  implicit val patientRecordSchema: Schema[RDPatientRecord] =
    Json.schema[RDPatientRecord]

}

object Schemas extends Schemas

