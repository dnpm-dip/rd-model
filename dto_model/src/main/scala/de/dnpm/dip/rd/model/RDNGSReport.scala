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
  Format
}



final case class Lab
(
  name: String
)

object Lab
{
  implicit val format: Format[Lab] =
    Json.format[Lab]
}

final case class RDNGSReport
(
  id: Id[RDNGSReport],
  patient: Reference[Patient],
  performingLab: Lab,
  recordedOn: Option[LocalDate],
  `type`: Coding[RDNGSReport.Type],
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
      Coding.System[Type]("rd/ngs-report/type")

    implicit val typeCodeSystem: CodeSystem[Type] =
      CodeSystem[Type](
//        uri = Coding.System[Type].uri,
        name = "NGS-Report-Type",
        title = Some("NGS-Report Type"),
        version = None,
        "panel"  -> "Panel",
        "exome"  -> "Exome",
        "genome" -> "Genome",
        "array"  -> "Array"
      )

    object Provider extends SingleCodeSystemProvider[Type](typeCodeSystem)

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

  implicit val formatMetaInfo: Format[MetaInfo] =
    Json.format[MetaInfo]

  implicit val format: Format[RDNGSReport] =
    Json.format[RDNGSReport]
  
}
