package de.dnpm.dip.rd.json.schema


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
import shapeless.{
  =:!=,
  Witness
}

trait Schemas extends BaseSchemas
{

  implicit val patientSchema: Schema[Patient] =
    Json.schema[Patient]
      .toDefinition("Patient")


  implicit val consentSchema: Schema[JsObject] = 
    Schema.`object`.Free[JsObject]()
      .toDefinition("Consent")


  implicit val diagCategoryCoding: Schema[Coding[RDDiagnosis.Category]] =
    Schema.`object`[Coding[RDDiagnosis.Category]](
      Field("code",codeSchema[Any]),
      Field("display",Schema.`string`,false),
      Field(
        "system",
        Schema.`enum`[String](
          Schema.`string`,
          Set(
            Coding.System[Orphanet],
            Coding.System[ICD10GM]
          )
          .map(cs => Value.str(cs.uri.toString))
        ),
        true
      ),
      Field("version",Schema.`string`,true)
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
      .toDefinition("PatientRecord")

}

object Schemas extends Schemas




/*
trait BaseJsonSchemas
{


  implicit def idSchema[T]: Schema[Id[T]] =
    Schema.`string`.asInstanceOf[Schema[Id[T]]]
      .toDefinition("Id")


  implicit def externalIdSchema[T]: Schema[ExternalId[T]] =
    Schema.`object`[ExternalId[T]](
      Field("value",Schema.`string`),
      Field("system",Schema.`string`(Schema.`string`.Format.`uri`),false),
    )
    .toDefinition("ExternalId")


  implicit def defaultReferenceSchema[T]: Schema[Reference[T]] =
    Schema.`object`[Reference[T]](
      Field("id",Schema.`string`),
    )
    .toDefinition("Reference")
//    Json.schema[Reference[T]]


  implicit val publicationReferenceSchema: Schema[Reference[Publication]] =
    Schema.`object`[Reference[Publication]](
      Field(
        "extId",
        Schema.`object`[ExternalId[Publication]](
          Field("value",Schema.`string`),
          Field("system",Schema.`string`(Schema.`string`.Format.`uri`),true,Coding.System[PubMed].uri.toString),
        ),
        false
      ),
      Field("uri",Schema.`string`(Schema.`string`.Format.`uri`),false),
    )
    .toDefinition("Reference[Publication]")


  implicit def enumCodingSchema[E <: Enumeration](
    implicit w: Witness.Aux[E]
  ): Schema[Coding[E#Value]] = {
    val name =
      w.value.getClass.getName
       .pipe {
         name =>
           val idx = name lastIndexOf "."
           if (idx > 0) name.substring(idx+1,name.length)
           else name  
      }
      .pipe { 
        name => 
          if (name endsWith "$") name.substring(0,name.length - 1)
          else name
      }
      .pipe(_.replace("$","."))

    Schema.`object`[Coding[E#Value]](
      Field(
        "code",
        Schema.`enum`[Code[E#Value]](
          Schema.`string`,
          w.value.values.map(_.toString).toSet.map(Value.str)
        )
      ),
      Field("display",Schema.`string`,false),
      Field("system",Schema.`string`(Schema.`string`.Format.`uri`),false),
      Field("version",Schema.`string`,false)
    )
    .toDefinition(s"Coding[$name]")
  }



  protected def codeSchema[T]: Schema[Code[T]] =
    Schema.`string`.asInstanceOf[Schema[Code[T]]]
      .toDefinition("Code")


  implicit def codingSchema[T](
    implicit notAny: T =:!= Any
  ): Schema[Coding[T]] =
    Schema.`object`[Coding[T]](
      Field("code",codeSchema[T]),
      Field("display",Schema.`string`,false),
      Field("system",Schema.`string`(Schema.`string`.Format.`uri`),false),
      Field("version",Schema.`string`,false)
    )
    .toDefinition("Coding")


  implicit val datePeriodSchema: Schema[Period[LocalDate]] =
    Json.schema[OpenEndPeriod[LocalDate]]
      .asInstanceOf[Schema[Period[LocalDate]]]


  import de.dnpm.dip.model.UnitOfTime.{Months,Years}

  implicit val ageSchema: Schema[Age] =
    Schema.`object`[Age](
      Field[Double]("value",Schema.`number`[Double]),
      Field[String](
        "unit",
        Schema.`enum`[String](
          Schema.`string`,
          Set(Months,Years).map(_.name).map(Value.str)
        )
      ),
    )
    .toDefinition("Age")

}
*/


