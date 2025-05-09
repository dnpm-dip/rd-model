package de.dnpm.dip.rd.model


import java.time.LocalDate
import de.dnpm.dip.coding.{
  CodedEnum,
  Coding,
  DefaultCodeSystem
}
import de.dnpm.dip.model.{
  Id,
  NGSReport,
  Patient,
  Reference
}
import play.api.libs.json.{
  Json,
  OFormat
}


final case class RDNGSReport
(
  id: Id[RDNGSReport],
  patient: Reference[Patient],
  issuedOn: LocalDate,
  `type`: Coding[NGSReport.Type.Value],
  sequencingInfo: Option[RDNGSReport.SequencingInfo],
  conclusion: Option[Coding[RDNGSReport.Conclusion.Value]],
  results: Option[RDNGSReport.Results]
)
extends NGSReport
{
  override val notes = None

  def variants: List[Variant] =
    results.flatMap(_.smallVariants).getOrElse(List.empty) ++
    results.flatMap(_.copyNumberVariants).getOrElse(List.empty) ++
    results.flatMap(_.structuralVariants).getOrElse(List.empty)
}

object RDNGSReport
{

  final case class SequencingInfo
  (
    platform: Coding[NGSReport.Platform.Value],
    kit: String
  )

  
  object Conclusion
  extends CodedEnum("dnpm-dip/rd/diagnostics/conclusion")
  with DefaultCodeSystem
  {

    val PartialPhenotype	               = Value("partial-phenotype")
    val StructuralVariantWithUnclearBreakpoint = Value("structural-variant-with-unclear-breakpoint")
    val UnclearVariantInDisease                = Value("unclear-variant-in-disease")
    val NoPathogenicVariantDetected            = Value("no-pathogenic-variant-detected")

    override val display =
      Map(
        PartialPhenotype                       -> "Phänotyp nicht komplett geklärt",
        StructuralVariantWithUnclearBreakpoint -> "Strukturvariante mit unklarem Bruchpunkt",
        UnclearVariantInDisease                -> "Unklare Variante in genetischer Erkankung",
        NoPathogenicVariantDetected            -> "keine pathogene Variante detektiert"
      )

  }


  final case class Results
  (
    autozygosity: Option[Autozygosity],
    smallVariants: Option[List[SmallVariant]],
    copyNumberVariants: Option[List[CopyNumberVariant]],
    structuralVariants: Option[List[StructuralVariant]],
  )

  implicit val formatSequencingInfo: OFormat[SequencingInfo] =
    Json.format[SequencingInfo]

  implicit val formatResults: OFormat[Results] =
    Json.format[Results]
  
  implicit val format: OFormat[RDNGSReport] =
    Json.format[RDNGSReport]
  
}
