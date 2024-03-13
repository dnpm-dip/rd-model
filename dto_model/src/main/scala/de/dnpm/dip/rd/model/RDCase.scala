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
  Episode,
  ExternalId,
  Patient,
  Period,
  Reference,
  TransferTAN
}
import play.api.libs.json.{
  Json,
  OFormat
}


sealed trait GestaltMatcher
object GestaltMatcher
{
  implicit val codingSystem: Coding.System[GestaltMatcher] =
    Coding.System[GestaltMatcher]("https://www.gestaltmatcher.org/")
}


final case class RDCase
(
  id: Id[RDCase],
  externalId: Option[ExternalId[RDCase]],
  patient: Reference[Patient],
  transferTan: Option[Id[TransferTAN]],
  gestaltMatcherId: Option[ExternalId[RDCase]], 
//  status: Coding[Episode.Status.Value],
//  recordedOn: Option[LocalDate],
//  period: Period[LocalDate],
  diagnoses: List[Reference[RDDiagnosis]],
  statusReason: Option[Coding[RDCase.StatusReason.Value]]
)
extends Episode


object RDCase
{

  object StatusReason
  extends CodedEnum("dnpm-dip/rd/case/status-reason")
  with DefaultCodeSystem
  {

    val NoSequencingRequested = Value("no-sequencing-requested")

    override val display =
      Map(
        NoSequencingRequested -> "Keine Sequenzierung veranlasst"
      )

  }

  implicit val format: OFormat[RDCase] =
    Json.format[RDCase]
}
