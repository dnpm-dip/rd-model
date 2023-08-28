package de.dnpm.dip.rd.model


import java.time.LocalDate
import cats.Applicative
import de.dnpm.dip.coding.{
  Coding,
  CodeSystem,
  CodedEnum,
  DefaultCodeSystem,
  CodeSystemProvider,
  CodeSystemProviderSPI,
  SingleCodeSystemProvider
}
import de.dnpm.dip.model.{
  Id,
  Reference,
  Patient,
  Diagnosis,
}
import play.api.libs.json.Json



final case class RDDiagnosis
(
  id: Id[RDDiagnosis],
  patient: Reference[Patient],
  recordedOn: Option[LocalDate],
  category: Coding[RDDiagnosis.Category],
  status: Coding[RDDiagnosis.Status.Value]
)
extends Diagnosis

object RDDiagnosis
{

  sealed trait Category

  object Category
  {

    implicit val categorySystem =
      Coding.System[Category]("rd/diagnosis/category")

    implicit val categoryCodeSystem =
      CodeSystem[Category](
        uri = Coding.System[Category].uri,
        name = "Diagnosis-Category",
        title = Some("Diagnosis-Category"),
        version = None,
        Seq(
          "neurodevelopmental",
          "neurological/neuromuscular",
          "organ abnormality",
          "haematopoiesis/immune system",
          "endocrine",
          "metabolic",
          "mitochondrial nutritional",
          "cardiovascular",
          "other"
        )
        .map(c => (c,c)): _*
      )

    object Provider extends SingleCodeSystemProvider[Category](categoryCodeSystem)

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }


  object Status
  extends CodedEnum("rd/diagnosis/status")
  with DefaultCodeSystem
  {

    val Solved          = Value("solved")
    val PartiallySolved = Value("partially-solved")
    val Unclear         = Value("unclear")
    val Unsolved        = Value("unsolved")

    implicit val format = Json.formatEnum(this)

    override val display = {
      case Solved          => "Gelöst"
      case PartiallySolved => "Teilweise gelöst"
      case Unclear         => "Unklar"
      case Unsolved        => "Ungelöst"
    }

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }


  implicit val format =
    Json.format[RDDiagnosis]
}
