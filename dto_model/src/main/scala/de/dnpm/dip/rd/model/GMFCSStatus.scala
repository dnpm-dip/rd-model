package de.dnpm.dip.rd.model


import java.time.LocalDate
import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  DefaultCodeSystem
}
import de.dnpm.dip.model.{
  Id,
  Reference,
  Observation,
  Patient
}
import play.api.libs.json.{
  Json,
  OFormat
}



object GMFCS
extends CodedEnum("Gross-Motor-Function-Classification-System")
with DefaultCodeSystem
{
  val One   = Value("I")
  val Two   = Value("II")
  val Three = Value("III")
  val Four  = Value("IV")
  val Five  = Value("V")

  override val display =
    Map(
      One   -> "Level I",
      Two   -> "Level II",
      Three -> "Level III",
      Four  -> "Level IV",
      Five  -> "Level V"
    )

}


final case class GMFCSStatus
(
  id: Id[GMFCSStatus],
  patient: Reference[Patient],
  effectiveDate: LocalDate,
  value: Coding[GMFCS.Value]
)
extends Observation[Coding[GMFCS.Value]]


object GMFCSStatus
{
  implicit val format: OFormat[GMFCSStatus] =
    Json.format[GMFCSStatus]
}

