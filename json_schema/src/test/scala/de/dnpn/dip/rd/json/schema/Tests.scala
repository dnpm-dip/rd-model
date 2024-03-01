package de.dnpm.dip.rd.json.schema


import scala.util.chaining._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import json.Schema
import json.schema.Version._
import com.github.andyglow.jsonschema.AsPlay._
import play.api.libs.json.Json.{
  prettyPrint,
  toJson
}
import de.dnpm.dip.rd.model._


class Tests extends AnyFlatSpec
{

  import Schemas._


  "JSON Schema derivation for MTBPatientRecord" must "have worked" in {

    Schema[RDPatientRecord].asPlay(Draft12("RD-Patient-Record"))
      .pipe(prettyPrint(_))
      .tap(println(_))

    succeed
  }

}

