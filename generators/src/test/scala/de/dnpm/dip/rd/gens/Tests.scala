package de.dnpm.dip.rd.gens


import scala.util.{
  Try,
  Random
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import de.ekut.tbi.generators.Gen
import de.dnpm.dip.rd.model.RDPatientRecord



class Tests extends AnyFlatSpec with Generators
{


  implicit val rnd: Random = new Random


  "Generating RDPatientRecord" must "have worked" in {

    assert(
      Try(Gen.of[RDPatientRecord].next) isSuccess
    )

  }

}
