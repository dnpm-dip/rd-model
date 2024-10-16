package de.dnpm.dip.rd.model.json


import java.io.{File,FileWriter}
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


  "JSON Schema derivation for RDPatientRecord" must "have worked" in {

    val schema =
      Schema[RDPatientRecord].asPlay(Draft12("RD-Patient-Record"))
        .pipe(prettyPrint(_))
        .tap(println(_))
/*      
      .tap { js =>
        val writer =
          new FileWriter(new File("/home/lucien/rd_patient_record_schema.json"))

        writer.write(js)
        writer.close
      }
*/

     schema must not contain ("Coding[")
  }

}

