package de.dnpm.dip.rd.model


import java.time.LocalDate
import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  DefaultCodeSystem
}
import de.dnpm.dip.coding.atc.ATC
import de.dnpm.dip.model.{
  Id,
  Therapy,
  SystemicTherapy,
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
  category: Option[Coding[RDTherapy.Category.Value]],
  `type`: Option[Coding[RDTherapy.Type.Value]],
  medication: Option[Set[Coding[ATC]]],
  period: Option[Period[LocalDate]],
  notes: Option[List[String]]
)
extends SystemicTherapy[ATC]
{
  override val reason = None
  override val status = Coding(Therapy.Status.Unknown)
  override val statusReason = None
  override val therapyLine = None
}


object RDTherapy
{

  object Category
  extends CodedEnum("dnpm-dip/rd/therapy/category")
  with DefaultCodeSystem
  {

    val Symptomatic = Value("symptomatic")
    val Causal      = Value("causal")

    override val display =
      Map(
        Symptomatic -> "Symptomatisch",
        Causal      -> "Kausal"
      )
  }


  object Type
  extends CodedEnum("dnpm-dip/rd/therapy/type")
  with DefaultCodeSystem
  {

    val SystemicMedication   = Value("systemic-medication")
    val TargetedMedication   = Value("targeted-medication")
    val PreventionMedication = Value("prevention-medication")
    val Genetic              = Value("genetic")
    val Prophylactic         = Value("prophylactic")
    val EarlyDetection       = Value("early-detection")
    val Combination          = Value("combination")
    val Nutrition            = Value("nutrition")
    val Other                = Value("other")

    override val display =
      Map(
        SystemicMedication   -> "Medikamentös systemisch",
        TargetedMedication   -> "Medikamentös zielgerichtet",
        PreventionMedication -> "Medikamentös Prävention",
        Genetic              -> "Gentherapie",
        Prophylactic         -> "Prophylaxe",
        EarlyDetection       -> "Früherkennung",
        Combination          -> "Kombinationstherapie",
        Nutrition            -> "Ernährung",
        Other                -> "Andere"
      )
  }

  implicit val format: OFormat[RDTherapy] =
    Json.format[RDTherapy]
}
