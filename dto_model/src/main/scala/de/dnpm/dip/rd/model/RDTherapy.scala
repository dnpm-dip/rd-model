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
  MedicationTherapy,
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
  medication: Option[Set[Coding[ATC]]],
  period: Option[Period[LocalDate]],
  notes: Option[String]
)
extends MedicationTherapy[ATC]
{
  val indication = None
  val status = Coding(Therapy.Status.Unknown)
  val statusReason = None
  val therapyLine = None
}

object RDTherapy
{

  object Category
  extends CodedEnum("dnpm-dip/rd/therapy/category")
  with DefaultCodeSystem
  {

    val SymptomaticTherapy      = Value("symptomatic-therapy")
    val SymptomaticMedication   = Value("symptomatic-medication-administration")
    val SymptomaticIntervention = Value("symptomatic-intervention")
    val CausalMedication        = Value("causal-medication-administration")
    val CausalIntervention      = Value("causal-intervention")

    override val display =
      Map(
        SymptomaticTherapy      -> "Symptomatische nicht-medikamentöse Therapie (Fördermaßnahmen)",
        SymptomaticMedication   -> "Symptomatische medikamentöse Therapie (bspw. antispastische Medikation)",
        SymptomaticIntervention -> "Symptomatische interventionelle Therapie (Operationen, Injektionen)",
        CausalMedication        -> "Kausale Therapie (medikamentös)",
        CausalIntervention      -> "Kausale Therapie (interventionell)"
      )
  }

  implicit val format: OFormat[RDTherapy] =
    Json.format[RDTherapy]
}
