package de.dnpm.dip.rd.model


import java.time.LocalDate
import cats.Applicative
import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  DefaultCodeSystem,
  CodeSystemProvider,
  CodeSystemProviderSPI,
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


sealed trait Lab

final case class RDNGSReport
(
  id: Id[RDNGSReport],
  patient: Reference[Patient],
  performingLab: Reference[Lab],
  issuedOn: LocalDate,
  sequencingType: Coding[NGSReport.SequencingType.Value],
  familyControls: Coding[RDNGSReport.FamilyControlLevel.Value],
  sequencingInfo: RDNGSReport.SequencingInfo,
  autozygosity: Option[Autozygosity],
  smallVariants: Option[List[SmallVariant]],
  copyNumberVariants: Option[List[CopyNumberVariant]],
  structuralVariants: Option[List[StructuralVariant]],
)
extends NGSReport
{
  def variants: List[Variant] =
    smallVariants.getOrElse(List.empty) ++
    copyNumberVariants.getOrElse(List.empty) ++
    structuralVariants.getOrElse(List.empty)
}

object RDNGSReport
{

  sealed trait FamilyControlLevel
  object FamilyControlLevel
  extends CodedEnum("dnpm-dip/rd/ngs-report/family-control-level")
  with DefaultCodeSystem
  {
    val single, duo, trio = Value
    val Gt3 = Value(">3")

    override val display =
      Map(
        single -> "single",
        duo    -> "duo",
        trio   -> "trio",  
        Gt3    -> ">3"
      )

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }

  final case class SequencingInfo
  (
    platform: Coding[NGSReport.Platform.Value],
    kit: String
  )


  implicit val formatSequencingInfo: OFormat[SequencingInfo] =
    Json.format[SequencingInfo]

  implicit val format: OFormat[RDNGSReport] =
    Json.format[RDNGSReport]
  
}
