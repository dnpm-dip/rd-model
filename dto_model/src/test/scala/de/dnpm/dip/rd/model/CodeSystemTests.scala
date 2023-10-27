package de.dnpm.dip.rd.model


import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import de.dnpm.dip.coding.{
  Coding,
  CodeSystemProvider
}


class CodeSystemTests extends AnyFlatSpec
{

  "Loading CodeSystems dynamically via CodeSystemProvider" must "have worked" in {

    CodeSystemProvider
      .getInstances[cats.Id]
      .map(_.uri)
      .toList must contain allOf (
        Coding.System[RDDiagnosis.Status.Value].uri,
//        Coding.System[RDDiagnosis.Category].uri,
        Coding.System[RDNGSReport.Type].uri,
        Coding.System[RDNGSReport.FamilyControlLevel].uri,
        Coding.System[Variant.ACMGClass].uri,
        Coding.System[Variant.ACMGCriteria].uri,
        Coding.System[Variant.Zygosity].uri,
        Coding.System[Variant.SegregationAnalysis].uri,
        Coding.System[Variant.InheritanceMode].uri,
        Coding.System[Variant.Significance].uri,
      )

  }

}
