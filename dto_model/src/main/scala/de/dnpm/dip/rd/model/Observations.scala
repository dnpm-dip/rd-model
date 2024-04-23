package de.dnpm.dip.rd.model



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
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.coding.hgvs.HGVS
import de.dnpm.dip.util.Displays
import de.dnpm.dip.model.{
  Id,
  ExternalId,
  Reference,
  Patient,
  Publication,
  Observation
}
import play.api.libs.json.{
  Json,
  Format,
  OFormat
}



final case class HPOTerm
(
  id: Id[HPOTerm],
  patient: Reference[Patient],
  value: Coding[HPO]
)
extends Observation[Coding[HPO]]

object HPOTerm
{
   implicit val format: OFormat[HPOTerm] =
    Json.format[HPOTerm]
}


final case class Autozygosity
(
  id: Id[Autozygosity],
  patient: Reference[Patient],
  value: Float
)
extends Observation[Float]

object Autozygosity
{
   implicit val format: OFormat[Autozygosity] =
    Json.format[Autozygosity]
}


