package de.dnpm.dip.rd.model.json


import java.time.LocalDate
import java.time.temporal.Temporal
import scala.util.chaining._
import cats.data.NonEmptyList
import play.api.libs.json.JsObject
import json.{
  Json,
  Schema
}
import com.github.andyglow.json.Value
import com.github.andyglow.jsonschema.AsPlay._
import com.github.andyglow.jsonschema.CatsSupport._
import Schema.`object`.Field
import json.schema.Version._
import de.dnpm.dip.coding.{
  Code,
  Coding,
  CodeSystem
}
import de.dnpm.dip.coding.icd.ICD10GM
import de.dnpm.dip.model.{
  Age,
  ExternalId,
  Id,
  Patient,
  Period,
  Publication,
  PubMed,
  OpenEndPeriod,
  Reference,
}
import de.dnpm.dip.model.json.BaseSchemas
import de.dnpm.dip.rd.model._


trait Schemas extends BaseSchemas
{

  implicit val patientSchema: Schema[Patient] =
    Json.schema[Patient]
      .toDefinition("Patient")


  implicit val consentSchema: Schema[JsObject] = 
    Schema.`object`.Free[JsObject]()
      .toDefinition("Consent")


  implicit val diagnosisCategoryCoding: Schema[Coding[RDDiagnosis.Category]] =
    Schema.`object`[Coding[RDDiagnosis.Category]](
      Field("code",codeSchema[Any]),
      Field("display",Schema.`string`,false),
      Field(
        "system",
        Schema.`enum`[String](
          Schema.`string`,
          Set(
            Coding.System[Orphanet],
            Coding.System[OMIM],
            Coding.System[ICD10GM]
          )
          .map(cs => Value.str(cs.uri.toString))
        ),
        true
      ),
      Field("version",Schema.`string`,false)
    )
    .toDefinition("Coding[Diagnosis.Category]")


  implicit val diagnosisSchema: Schema[RDDiagnosis] =
    Json.schema[RDDiagnosis]
      .toDefinition("Diagnosis")


  implicit val caseSchema: Schema[RDCase] =
    Json.schema[RDCase]
      .toDefinition("Case")


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


  implicit val therapySchema: Schema[RDTherapy] =
    Json.schema[RDTherapy]
      .toDefinition("Therapy")


  implicit val patientRecordSchema: Schema[RDPatientRecord] =
    Json.schema[RDPatientRecord]

}

object Schemas extends Schemas

