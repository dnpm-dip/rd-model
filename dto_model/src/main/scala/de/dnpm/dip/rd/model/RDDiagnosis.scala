package de.dnpm.dip.rd.model


import java.net.URI
import java.time.LocalDate
import cats.Applicative
import cats.data.NonEmptyList
import de.dnpm.dip.coding.{
  Coding,
  CodeSystem,
  CodedEnum,
  DefaultCodeSystem,
  CodeSystemProvider,
  CodeSystemProviderSPI,
  SingleCodeSystemProvider,
  ValueSet,
  ValueSetProvider,
  ValueSetProviderSPI,
  Version
}
import de.dnpm.dip.coding.icd.ICD10GM
import de.dnpm.dip.model.{
  Id,
  Reference,
  Patient,
  Diagnosis,
  Age
}
import play.api.libs.json.{
  Json,
  Format,
  OFormat
}
import shapeless.{
  :+:,
  CNil
}


final case class RDDiagnosis
(
  id: Id[RDDiagnosis],
  patient: Reference[Patient],
  recordedOn: Option[LocalDate],
  categories: NonEmptyList[Coding[RDDiagnosis.Category]],
  onsetAge: Option[Age],
  prenatal: Boolean,
  status: Coding[RDDiagnosis.Status.Value]
)
extends Diagnosis

object RDDiagnosis
{

  type Category =
    Orphanet :+: ICD10GM :+: OMIM :+: CNil


  object Category
  {

    implicit val system: Coding.System[Category] =
      Coding.System[Category]("dnpm-dip/rd/disease-category")

    private val version: String = "-" 

    private lazy val ordo =
      Orphanet.Ordo
        .getInstance[cats.Id]
        .get
        .latest

    private lazy val omim =
      OMIM.Catalog
        .getInstance[cats.Id]
        .get
        .latest

    private lazy val icd10gm =
      ICD10GM.Catalogs
        .getInstance[cats.Id]
        .get
        .latest

    lazy val valueSet: ValueSet[Category] =
      ValueSet(
        uri = Coding.System[Category].uri,
        name = "rare-disease-category",
        title = Some("Rare Disease Category"),
        date = None,
        version = None,
        codings =
          ordo.concepts.map(_.toCodingOf[Category]) ++
          icd10gm.concepts.map(_.toCodingOf[Category]) ++
          omim.concepts.map(_.toCodingOf[Category])
      )

    class ProviderSPI extends ValueSetProviderSPI
    {
      def getInstance[F[_]]: ValueSetProvider[Any,F,Applicative[F]] =
        new Provider[F]
    }


    private class Provider[F[_]] extends ValueSetProvider[Category,F,Applicative[F]]
    {

      import cats.syntax.applicative._
      import cats.syntax.functor._

      val uri: URI =
        Coding.System[Category].uri
    
      val versionOrdering: Ordering[String] =
        Version.Unordered
    
      def versions(
        implicit env: Applicative[F]
      ): F[NonEmptyList[String]] =
        NonEmptyList.of(version).pure
    
      def latestVersion(
        implicit env: Applicative[F]
      ): F[String] =
        version.pure
    
      def get(
        version: String
      )(
        implicit env: Applicative[F]
      ): F[Option[ValueSet[Category]]] =
        latest.map(Some(_))
    
      def latest(
        implicit env: Applicative[F]
      ): F[ValueSet[Category]] =
        valueSet.pure

    }

  }


  object Status
  extends CodedEnum("dnpm-dip/rd/diagnosis/status")
  with DefaultCodeSystem
  {

    val Solved          = Value("solved")
    val PartiallySolved = Value("partially-solved")
    val Unclear         = Value("unclear")
    val Unsolved        = Value("unsolved")

    implicit val format: Format[Status.Value] = 
      Json.formatEnum(this)

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


  import de.dnpm.dip.util.json._

  implicit val format: OFormat[RDDiagnosis] =
    Json.format[RDDiagnosis]
}
