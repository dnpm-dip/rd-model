package de.dnpm.dip.rd.model.tests


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import de.dnpm.dip.coding.{
  Coding,
  ValueSetProvider
}
import de.dnpm.dip.rd.model.RDDiagnosis


class CodeSystemTests extends AnyFlatSpec
{

  "Loading ValueSet dynamically via ValueSetProvider" must "have worked" in {

    import RDDiagnosis.Category._

    ValueSetProvider
      .getInstances[cats.Id]
      .map(_.uri)
      .toList must contain (
        Coding.System[RDDiagnosis.Category].uri
      )

  }

}
