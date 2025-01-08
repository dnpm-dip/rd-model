package de.dnpm.dip.orphanet.impl


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
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



  "ORDO" must "have been successfully loaded as CodeSystemProvider[Any]" in {

    CodeSystemProvider
      .getInstances[Id]
      .map(_.uri)
      .toList must contain (Coding.System[Orphanet].uri)

  }

  "CodeSystem[Orphanet]" must "have a defined version" in {

    ordo.version must be (defined)

  }

  it must "contain non-empty concept list" in {

    ordo.concepts must not be empty

  }

  it must "have defined code and display values on all concepts" in {

    all (ordo.concepts.map(_.code.value)) must not be empty
    all (ordo.concepts.map(_.display.trim)) must not be empty

  }


  it must "contain ICD-10-Codes on some concepts" in {

    atLeast (1, ordo.concepts.map(_.icd10Codes)) must not be (empty)

  }


  it must "contain ICD-11-Codes on some concepts" in {

    atLeast (1, ordo.concepts.map(_.icd11Codes)) must not be (empty)

  }


  it must "contain 'alternative terms' on some concepts" in {

    atLeast (1, ordo.concepts.map(_.alternativeTerms)) must not be empty

  }

  it must "contain 'super-classes' on some concepts" in {

    atLeast (1, ordo.concepts.map(_.superClasses)) must not be empty

  }

  it must "contain 'sub-classes' (children) on some concepts" in {

    atLeast (1, ordo.concepts.map(_.children)) must not be empty

  }


}
