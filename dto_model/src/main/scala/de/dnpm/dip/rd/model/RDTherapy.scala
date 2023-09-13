package de.dnpm.dip.rd.model


import de.dnpm.dip.model.{
  Id,
  Reference,
  Patient,
}
import play.api.libs.json.{
  Json,
  Format
}


final case class RDTherapy
(
  id: Id[RDTherapy],
  patient: Reference[Patient],
  notes: String
)

object RDTherapy
{

  implicit val format: Format[RDTherapy] =
    Json.format[RDTherapy]
}
