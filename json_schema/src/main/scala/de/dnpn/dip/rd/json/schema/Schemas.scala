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
import json.schema.Version._
import de.dnpm.dip.coding.{
  Code,
  Coding,
  CodeSystem
}
import de.dnpm.dip.model.{
  Age,
  ExternalId,
  Id,
  Patient,
  Period,
  OpenEndPeriod,
  Reference,
  IdReference,
}
import de.dnpm.dip.rd.model._
import shapeless.{
  =:!=,
  Witness
}


trait BaseJsonSchemas
{

  import Schema.`object`.Field


  implicit def idSchema[T]: Schema[Id[T]] =
    Schema.`string`.asInstanceOf[Schema[Id[T]]]
      .toDefinition("Id")


  implicit def externalIdSchema[T]: Schema[ExternalId[T]] =
    Schema.`object`[ExternalId[T]](
      Field("value",Schema.`string`),
      Field("system",Schema.`string`(Schema.`string`.Format.`uri`),false),
    )
    .toDefinition("ExternalId")


  implicit def referenceSchema[T]: Schema[Reference[T]] =
    Schema.`object`[Reference[T]](
      Field("id",Schema.`string`),
    )
    .toDefinition("Reference")


  def enumCodeSchema[E <: Enumeration](
    implicit w: Witness.Aux[E]
  ): Schema[Code[E#Value]] =
    Schema.`enum`[Code[E#Value]](
      Schema.`string`,
      w.value.values.map(_.toString).toSet.map(Value.str)
    )


  def codeSchema[T](cs: CodeSystem[T]): Schema[Code[T]] =
    Schema.`enum`[Code[T]](
      Schema.`string`,
      cs.concepts.map(_.code.value).toSet.map(Value.str)
    )


  implicit def codeSchema[T]: Schema[Code[T]] =
    Schema.`string`.asInstanceOf[Schema[Code[T]]]
      .toDefinition("Code")


  def enumCodingSchema[E <: Enumeration](
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
      Field("code",enumCodeSchema[E]),
      Field("display",Schema.`string`,false),
      Field("system",Schema.`string`(Schema.`string`.Format.`uri`),false),
      Field("version",Schema.`string`,false)
    )
    .toDefinition(s"Coding[$name]")
  }
 

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


  implicit val anyCodingSchema: Schema[Coding[Any]] =
    Schema.`object`[Coding[Any]](
      Field("code",codeSchema[Any]),
      Field("display",Schema.`string`,false),
      Field("system",Schema.`string`(Schema.`string`.Format.`uri`)),
      Field("version",Schema.`string`,false)
    )
    .toDefinition("Coding[Any]")


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



trait Schemas extends BaseJsonSchemas
{

  implicit val patientSchema: Schema[Patient] =
    Json.schema[Patient]


  implicit val consentSchema: Schema[JsObject] = 
    Schema.`object`.Free[JsObject]()


  implicit val diagnosisSchema: Schema[RDDiagnosis] =
    Json.schema[RDDiagnosis]


  implicit val caseSchema: Schema[RDCase] =
    Json.schema[RDCase]


  implicit val hpoTermSchema: Schema[HPOTerm] =
    Json.schema[HPOTerm]

 
  implicit val SmallVariantSchema: Schema[SmallVariant] =
    Json.schema[SmallVariant]


  implicit val StructuralVariantSchema: Schema[StructuralVariant] =
    Json.schema[StructuralVariant]


  implicit val CopyNumberVariantSchema: Schema[CopyNumberVariant] =
    Json.schema[CopyNumberVariant]


  implicit val ngsReportSchema: Schema[RDNGSReport] =
    Json.schema[RDNGSReport]


  implicit val patientRecordSchema: Schema[RDPatientRecord] =
    Json.schema[RDPatientRecord]

}

object Schemas extends Schemas
