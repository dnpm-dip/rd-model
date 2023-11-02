package de.dnpm.dip.rd.model


import java.time.LocalDate
import cats.Applicative
import de.dnpm.dip.coding.{
  Coding,
  CodeSystem,
  CodeSystemProvider,
  CodeSystemProviderSPI,
  SingleCodeSystemProvider
}

import de.dnpm.dip.model.{
  Id,
  Reference,
  Patient,
}
import play.api.libs.json.{
  Json,
  OFormat
}



final case class Lab
(
  name: String
)

object Lab
{
  implicit val format: OFormat[Lab] =
    Json.format[Lab]
}

final case class RDNGSReport
(
  id: Id[RDNGSReport],
  patient: Reference[Patient],
  performingLab: Lab,
  recordedOn: Option[LocalDate],
  `type`: Coding[RDNGSReport.Type],
  familyControls: Coding[RDNGSReport.FamilyControlLevel],
  metaInfo: RDNGSReport.MetaInfo,
  autozygosity: Option[Autozygosity],
  variants: Option[List[Variant]]
)


object RDNGSReport
{

  sealed trait Type
  object Type
  {

    implicit val typeSystem: Coding.System[Type] =
      Coding.System[Type]("dnpm-dip/rd/ngs-report/type")

    implicit val typeCodeSystem: CodeSystem[Type] =
      CodeSystem[Type](
        name = "NGS-Report-Type",
        title = Some("NGS-Report Type"),
        version = None,
        "panel"  -> "Panel",
        "exome"  -> "Exome",
        "genome" -> "Genome",
        "array"  -> "Array"
      )

    object Provider extends SingleCodeSystemProvider[Type]

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }

  sealed trait FamilyControlLevel
  object FamilyControlLevel
  {

    implicit val familyControlLevelSystem: Coding.System[FamilyControlLevel] =
      Coding.System[FamilyControlLevel]("dnpm-dip/rd/ngs-report/family-control-level")

    implicit val familyControlLevelCodeSystem: CodeSystem[FamilyControlLevel] =
      CodeSystem[FamilyControlLevel](
        name = "FamilyControlLevel",
        title = Some("Family Control Level"),
        version = None,
        concepts =
          Seq(
            "single",
            "duo",
            "trio",  
            ">3"
          )
          .map(c => (c,c)): _*
      )

    object Provider extends SingleCodeSystemProvider[FamilyControlLevel]

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }



  final case class MetaInfo
  (
    sequencingType: String,
    kit: String
  )

  implicit val formatMetaInfo: OFormat[MetaInfo] =
    Json.format[MetaInfo]

  implicit val format: OFormat[RDNGSReport] =
    Json.format[RDNGSReport]
  
}
