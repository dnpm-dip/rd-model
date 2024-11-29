package de.dnpm.dip.rd.model


import java.time.LocalDate
import de.dnpm.dip.model.{
  Duration,
  Id,
  Patient,
  Period,
  Reference,
}
import play.api.libs.json.{
  Json,
  OFormat
}


final case class Hospitalization
(
  id: Id[Hospitalization],
  patient: Reference[Patient],
  period: Option[Period[LocalDate]],
  duration: Duration
)


object Hospitalization
{
  implicit val format: OFormat[Hospitalization] =
    Json.format[Hospitalization]
}
