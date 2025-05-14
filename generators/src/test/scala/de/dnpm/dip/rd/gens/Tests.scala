package de.dnpm.dip.rd.gens


import scala.util.Random
import scala.util.chaining._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import de.ekut.tbi.generators.Gen
import de.dnpm.dip.rd.model.RDPatientRecord
import de.dnpm.dip.rd.model.json.Schemas
import play.api.libs.json.Json.{
  toJson,
  stringify
}
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.{
  JsonSchemaFactory,
  SpecVersion
}
import scala.jdk.CollectionConverters._
import json.Schema
import json.schema.Version._
import com.github.andyglow.jsonschema.AsPlay._


class Tests extends AnyFlatSpec
with Generators
with Schemas
{


  implicit val rnd: Random = new Random


  "Generated RDPatientRecord" must "conform to the JSON schema" in {

    val schema =
      JsonSchemaFactory
        .getInstance(SpecVersion.VersionFlag.V202012)
        .getSchema(
          Schema[RDPatientRecord]
            .asPlay(Draft12("https://dnpm-dip/rd/patient-record-schema.json"))
            .pipe(stringify)
        )

    val jsonRecord =
      new ObjectMapper().readTree(
        Gen.of[RDPatientRecord].next
          .pipe(toJson(_))
          .pipe(stringify)
      )

    val errors =
      schema.validate(jsonRecord)
        .asScala
        .tap(_.foreach(msg => println(msg.getMessage)))

    errors must be (empty)

  }

}
