package de.dnpm.dip.rd.gens


import scala.util.{
  Try,
  Random
}
import org.scalatest.flatspec.AnyFlatSpec
import de.ekut.tbi.generators.Gen
import de.dnpm.dip.rd.model.RDPatientRecord



class Tests extends AnyFlatSpec with Generators
{

  implicit val rnd: Random = new Random


  "Generating RDPatientRecord" must "have worked" in {
/*
    import play.api.libs.json.Json._
    import scala.util.chaining._

    val patientRecord =
      Gen.of[RDPatientRecord].next

    patientRecord
      .pipe(toJson(_))
      .pipe(prettyPrint)  
      .tap(println)
*/    

    assert(
      Try(Gen.of[RDPatientRecord].next) isSuccess
    )

  }

}
