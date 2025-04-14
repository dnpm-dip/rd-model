package de.dnpm.dip.rd.model


import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  DefaultCodeSystem
}
import play.api.libs.json.{
  Json,
  OFormat
}


final case class Hospitalization
(
  numberOfStays: Coding[Hospitalization.NumberOfStays.Value],
  numberOfDays: Coding[Hospitalization.NumberOfDays.Value]
)


object Hospitalization
{

  object NumberOfStays
  extends CodedEnum("dnpm-dip/rd/hospitalization/number-of-stays")
  with DefaultCodeSystem
  {
    val Zero            = Value("none")
    val UpToFive        = Value("up-to-five")
    val UpToTen         = Value("up-to-ten")
    val UpToFifteen     = Value("up-to-fifteen")
    val OverFifteen     = Value("over-fifteen")
    val Unknown         = Value("unknown")

    override val display =
      Map(
        Zero        -> "Keine",
        UpToFive    -> "Bis zu 5",
        UpToTen     -> "Bis zu 10",
        UpToFifteen -> "Bis zu 15",
        OverFifteen -> "Über 15",
        Unknown     -> "Unbekannt"
      )
  }


  object NumberOfDays
  extends CodedEnum("dnpm-dip/rd/hospitalization/number-of-days")
  with DefaultCodeSystem
  {
    val Zero          = Value("none")
    val UpToFive      = Value("up-to-five")
    val UpToFifteen   = Value("up-to-tifteen")
    val UpToFifty     = Value("up-to-fifty")
    val OverFifty     = Value("over-fifty")
    val Unknown       = Value("unknown")

    override val display =
      Map(
        Zero        -> "Keine",
        UpToFive    -> "Bis zu 5",
        UpToFifteen -> "Bis zu 15",
        UpToFifty   -> "Bis zu 50",
        OverFifty   -> "Über 50",
        Unknown     -> "Unbekannt"
      )
  }

  implicit val format: OFormat[Hospitalization] =
    Json.format[Hospitalization]
}


/*
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
*/
