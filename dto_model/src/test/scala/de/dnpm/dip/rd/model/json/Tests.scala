package de.dnpm.dip.rd.model.json


import scala.util.chaining._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import json.Schema
import json.schema.Version._
import com.github.andyglow.jsonschema.AsPlay._
import play.api.libs.json.Json
import de.dnpm.dip.rd.model._


class Tests extends AnyFlatSpec
{

  import Schemas._


  "JSON Schema derivation for RDPatientRecord" must "have worked" in {

    val schema =
      Schema[RDPatientRecord].asPlay(Draft12("RD-Patient-Record"))
        .pipe(Json.prettyPrint(_))
//        .tap(println(_))

     schema must not contain ("Coding[")
     schema must contain noneOf ("head","tail")
  }

}

