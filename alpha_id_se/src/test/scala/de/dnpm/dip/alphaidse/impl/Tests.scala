package de.dnpm.dip.alphaidse.impl


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import cats.Id
import de.dnpm.dip.rd.model.AlphaIDSE
import de.dnpm.dip.coding.{
  Coding,
  CodeSystemProvider
}


class Tests extends AnyFlatSpec
{

  import AlphaIDSE.extensions._


  val catalogs =
    AlphaIDSE.Catalogs
      .getInstance[Id]

  lazy val alphaID =
    catalogs.get.latest



  "Alpha-ID-SE" must "have been successfully loaded as CodeSystemProvider[Any]" in {

    CodeSystemProvider
      .getInstances[Id]
      .map(_.uri)
      .toList must contain (Coding.System[AlphaIDSE].uri)

  }

  it must "have been successfully loaded as AlphaIDSE.Catalog" in {
    
    catalogs.isSuccess mustBe true

  }


  it must "contain non-empty list of entries" in {

    alphaID.concepts must not be (empty)

  }


  it must "pass check that Primary Code 1 or 2 is present on some nodes" in {

    atLeast (1, alphaID.concepts.map(c => c.primaryCode1.orElse(c.primaryCode2))) must be (defined)

  }

}
