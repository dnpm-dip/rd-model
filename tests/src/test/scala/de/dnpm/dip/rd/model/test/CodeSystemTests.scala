package de.dnpm.dip.rd.model.tests


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import org.scalatest.Inspectors._
import de.dnpm.dip.coding.{
  Coding,
  ValueSetProvider
}
import de.dnpm.dip.rd.model.RDDiagnosis


class CodeSystemTests extends AnyFlatSpec
{

  import play.api.libs.json._
  import Json._
  import scala.util.chaining._


  "Loading ValueSet dynamically via ValueSetProvider" must "have worked" in {

    import RDDiagnosis.Systems._

    ValueSetProvider
      .getInstances[cats.Id]
      .map(_.uri)
      .toList must contain (
        Coding.System[RDDiagnosis.Systems].uri
      )

  }

  "Serialized Codings of ValueSet 'RDDiagnosis.Systems'" must "all have 'system' attribute" in {

    RDDiagnosis.Systems
      .valueSet
      .codings
      .pipe(toJson(_))
      .pipe(_.as[JsArray].value)
      .pipe(
        forAll(_){ js => (js \ "system").asOpt[String] must be (defined) }
      )
   
  }

}
