package de.dnpm.dip.rd.model


import java.time.LocalDate
import cats.Applicative
import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  CodeSystem,
  DefaultCodeSystem,
  CodeSystemProvider,
  CodeSystemProviderSPI,
  SingleCodeSystemProvider
}

import de.dnpm.dip.model.{
  Id,
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
  recordedOn: Option[LocalDate],
  `type`: Coding[RDNGSReport.Type.Value],
  familyControls: Coding[RDNGSReport.FamilyControlLevel.Value],
  sequencingInfo: RDNGSReport.SequencingInfo,
  autozygosity: Option[Autozygosity],
  smallVariants: Option[List[SmallVariant]],
  copyNumberVariants: Option[List[CopyNumberVariant]],
  structuralVariants: Option[List[StructuralVariant]],
)
{
  def variants: List[Variant] =
    smallVariants.getOrElse(List.empty) ++
    copyNumberVariants.getOrElse(List.empty) ++
    structuralVariants.getOrElse(List.empty)
}

object RDNGSReport
{

  sealed trait Type
  object Type
  extends CodedEnum("dnpm-dip/rd/ngs-report/type")
  with DefaultCodeSystem
  {
    val panel, exome, array = Value            

    val genomeShortRead = Value("genome-short-read")
    val genomeLongRead  = Value("genome-long-read")

    override val display =
      Map(
        panel           -> "Panel",
        exome           -> "Exome",
        genomeShortRead -> "Genome short-read",
        genomeLongRead  -> "Genome long-read",
        array           -> "Array"
      )

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }

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
        duo    ->"duo",
        trio   -> "trio",  
        Gt3    -> ">3"
      )

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }


  sealed trait Platform
  object Platform
  {
    implicit val system: Coding.System[Platform] =
      Coding.System[Platform]("/dnpm-dip/rd/ngs/sequencing/platform")

    implicit val codeSystem: CodeSystem[Platform] =
      CodeSystem[Platform](
        name = "Sequencing-Platform",
        title = Some("Sequencing Platform"),
        version = None,
        "Illumina"     -> "Illumina",
        "ONT"          -> "ONT",
        "10X-Genomics" -> "10X Genomics",
        "PacBio"       -> "PacBio"
      )

    object Provider extends SingleCodeSystemProvider[Platform]

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }


  final case class SequencingInfo
  (
    platform: Coding[Platform],
    kit: String
  )


  implicit val formatSequencingInfo: OFormat[SequencingInfo] =
    Json.format[SequencingInfo]

  implicit val format: OFormat[RDNGSReport] =
    Json.format[RDNGSReport]
  
}
