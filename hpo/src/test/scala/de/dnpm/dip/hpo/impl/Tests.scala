package de.dnpm.dip.hpo.impl



import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import cats.Id
import de.dnpm.dip.rd.model.HPO
import de.dnpm.dip.coding.{
  Coding,
  CodeSystemProvider
}
/*
import play.api.libs.json.Json.{
  toJson,
  prettyPrint
}
*/

class Tests extends AnyFlatSpec
{

  val ontology =
    HPO.Ontology
      .getInstance[Id]

  lazy val hpo =
    ontology.get.latest


  "HPO" must "have been successfully loaded as CodeSystemProvider[Any]" in {

    CodeSystemProvider
      .getInstances[Id]
      .map(_.uri)
      .toList must contain (Coding.System[HPO].uri)

  }


  it must "have been successfully loaded as CodeSystem[HPO]" in {
    
    ontology.isSuccess mustBe true

  }


  it must "contain non-empty list of entries" in {

    hpo.concepts must not be (empty)
  }

}
