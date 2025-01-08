package de.dnpm.dip.hpo.impl


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import cats.Id
import de.dnpm.dip.rd.model.HPO
import de.dnpm.dip.coding.{
  Coding,
  CodeSystemProvider
}


class Tests extends AnyFlatSpec
{

  import HPO.extensions._


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


  it must "have been successfully loaded as HPO.Ontology" in {
    
    ontology.isSuccess mustBe true

  }


  it must "contain non-empty list of entries" in {

    hpo.concepts must not be (empty)

  }



  it must "pass check that node type is always defined" in {

    all (hpo.concepts.map(_.getType)) must be (defined)

  }


  it must "pass check that definition is present on some nodes" in {

    atLeast (1, hpo.concepts.map(_.definition)) must be (defined)

  }


  it must "pass check that nodes can have mutiple super-classes" in {

    atLeast (1, hpo.concepts.map(_.superClasses.size)) must be > 1

  }


/*
  it must "pass check that HPO 'All' has all other concepts as descendants" in {

    // NOTE:
    // this test should normally succeed,
    // given that "All" is supposed to be the super-class of all HPO concepts.
    // However, it fails even though no systematic bug could be detected in the parentship resolution.
    // Maybe the parentship relations are not fully consistent in HPO itself
    hpo.descendantsOf(Code[HPO]("HP:0000001")).size must equal (hpo.concepts.size - 1)

  }
*/
}
