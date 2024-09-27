package de.dnpm.dip.rd.model


import java.time.LocalDate
import cats.Applicative
import de.dnpm.dip.coding.{
  CodedEnum,
  Coding,
  DefaultCodeSystem
}
import de.dnpm.dip.model.{
  Id,
  EpisodeOfCare,
  ExternalId,
  Patient,
  Period,
  Reference
}
import play.api.libs.json.{
  Json,
  OFormat
}


final case class RDEpisodeOfCare
(
  id: Id[RDEpisodeOfCare],
  patient: Reference[Patient],
  period: Period[LocalDate]
)
extends EpisodeOfCare
{
  val diagnoses = None
}


object RDEpisodeOfCare
{
  implicit val format: OFormat[RDEpisodeOfCare] =
    Json.format[RDEpisodeOfCare]
}
