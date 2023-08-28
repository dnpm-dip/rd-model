package de.dnpm.dip.rd.gens


import scala.util.Try
import org.scalatest.flatspec.AnyFlatSpec
import de.ekut.tbi.generators.Gen
import de.dnpm.dip.rd.model.RDPatientRecord



class Tests extends AnyFlatSpec with Generators
{

  implicit val rnd =
    new scala.util.Random


  "Generating RDPatientRecord" must "have worked" in {

    assert(
      Try(Gen.of[RDPatientRecord].next) isSuccess
    )

  }

}
