package de.dnpm.dip.rd.model


import java.time.LocalDate
import de.dnpm.dip.coding.Coding
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
  sequencingInfo: RDNGSReport.SequencingInfo,
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
