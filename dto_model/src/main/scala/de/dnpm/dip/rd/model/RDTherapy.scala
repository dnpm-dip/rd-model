package de.dnpm.dip.rd.model


import java.time.LocalDate
import de.dnpm.dip.coding.Coding
import de.dnpm.dip.coding.atc.ATC
import de.dnpm.dip.model.{
  Id,
  Patient,
  Period,
  Reference
}
import play.api.libs.json.{
  Json,
  OFormat
}


final case class RDTherapy
(
  id: Id[RDTherapy],
  patient: Reference[Patient],
  basedOn: Option[Reference[RDTherapyRecommendation]],
  recordedOn: LocalDate,
//  `type`: Coding[RDTherapyRecommendation.Type.Value],
  medication: Option[Set[Coding[ATC]]],
  period: Option[Period[LocalDate]],
  notes: Option[String]
)

object RDTherapy
{

  implicit val format: OFormat[RDTherapy] =
    Json.format[RDTherapy]
}
