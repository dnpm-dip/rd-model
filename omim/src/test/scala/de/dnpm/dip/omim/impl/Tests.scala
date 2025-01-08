package de.dnpm.dip.omim.impl


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import cats.Id
import de.dnpm.dip.rd.model.OMIM
import de.dnpm.dip.coding.{
  Coding,
  CodeSystemProvider
}


class Tests extends AnyFlatSpec
{

  val omimTry =
    OMIM.Catalog
      .getInstance[Id]

  lazy val omim =
    omimTry.get.latest



  "OMIM" must "have been successfully loaded as CodeSystemProvider[Any]" in {

    CodeSystemProvider
      .getInstances[Id]
      .map(_.uri)
      .toList must contain (Coding.System[OMIM].uri)

  }


  it must "have been successfully loaded as OMIM.Catalog" in {
    
    omimTry.isSuccess mustBe true

  }


  it must "contain non-empty list of entries" in {

    omim.concepts must not be (empty)

  }

  "Property 'entry type'" must "be defined on all entries" in {

    all (omim.concepts.map(_.get(OMIM.EntryType).flatMap(_.headOption))) must be (defined)

  }


}
