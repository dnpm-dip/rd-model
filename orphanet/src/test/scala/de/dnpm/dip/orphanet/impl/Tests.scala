package de.dnpm.dip.hpo.impl


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import org.scalatest.Inspectors._
import cats.Id
import de.dnpm.dip.rd.model.Orphanet
import de.dnpm.dip.coding.{
  Coding,
  CodeSystemProvider
}


class Tests extends AnyFlatSpec
{

  import Orphanet.extensions._


  val ordoProvider =
    Orphanet.Ordo
      .getInstance[Id]

  lazy val ordo =
    ordoProvider.get.latest



  "Orphanet.Ordo" must "have been successfully loaded as CodeSystemProvider[Any]" in {

    CodeSystemProvider
      .getInstances[Id]
      .map(_.uri)
      .toList must contain (Coding.System[Orphanet].uri)

  }


  "ORDO" must "have been correctly parsed as CodeSystem[Orphanet]" in {

    ordo.version must be (defined)

    ordo.concepts must not be empty

    all (ordo.concepts.map(_.code.value)) must not be empty

    atLeast (1, ordo.concepts.map(_.icd10Code)) must be (defined)

    atLeast (1, ordo.concepts.map(_.alternativeTerms)) must not be empty

//    atLeast (1, ordo.concepts.flatMap(_.icd10Code).map(_.value)) must contain ('+')
  }


}
