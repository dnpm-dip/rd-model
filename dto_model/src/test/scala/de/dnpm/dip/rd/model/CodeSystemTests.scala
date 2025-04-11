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
        Coding.System[RDDiagnosis.VerificationStatus.Value].uri,
        Coding.System[RDDiagnosis.FamilyControlLevel.Value].uri,
        Coding.System[ACMG.Class.Value].uri,
        Coding.System[ACMG.Criterion.Type.Value].uri,
        Coding.System[ACMG.Criterion.Modifier.Value].uri,
        Coding.System[Variant.Zygosity.Value].uri,
        Coding.System[Variant.SegregationAnalysis.Value].uri,
        Coding.System[Variant.ModeOfInheritance.Value].uri,
        Coding.System[Variant.Significance.Value].uri,
      )

  }

}
