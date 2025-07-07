package de.dnpm.dip.rd.model


import java.net.URI
import java.time.{
  YearMonth,
  LocalDate
}
import cats.Applicative
import cats.data.NonEmptyList
import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  DefaultCodeSystem,
  CodeSystemProvider,
  CodeSystemProviderSPI,
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
}
import play.api.libs.json.{
  Json,
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
  recordedOn: LocalDate,
  onsetDate: Option[YearMonth],
  familyControlLevel: Coding[RDDiagnosis.FamilyControlLevel.Value],
  verificationStatus: Coding[RDDiagnosis.VerificationStatus.Value],
  codes: NonEmptyList[Coding[RDDiagnosis.Systems]],
  missingCodeReason: Option[Coding[RDDiagnosis.MissingCodeReason.Value]],
  notes: Option[List[String]]
)
extends Diagnosis

object RDDiagnosis
{

  type Systems = Orphanet :+: ICD10GM :+: AlphaIDSE :+: CNil


  object Systems
  {

    implicit val system: Coding.System[Systems] =
      Coding.System[Systems]("dnpm-dip/rd/diagnosis/code-systems")

    private val version: String = "-" 

    private lazy val ordo =
      Orphanet.Ordo
        .getInstance[cats.Id]
        .get
        .latest

    private lazy val alphaId =
      AlphaIDSE.Catalogs
        .getInstance[cats.Id]
        .get
        .latest

    private lazy val icd10gm =
      ICD10GM.Catalogs
        .getInstance[cats.Id]
        .get
        .latest

    lazy val valueSet: ValueSet[Systems] =
      ValueSet(
        uri = Coding.System[Systems].uri,
        name = "rare-disease-code-systems",
        title = Some("Rare Disease CodeSystems"),
        date = None,
        version = None,
        codings =
          ordo.concepts.map(_.toCodingOf[Systems]) ++
          icd10gm.concepts.map(_.toCodingOf[Systems]) ++
          alphaId.concepts.map(_.toCodingOf[Systems])
      )

    class ProviderSPI extends ValueSetProviderSPI
    {
      def getInstance[F[_]]: ValueSetProvider[Any,F,Applicative[F]] =
        new Provider[F]
    }


    private class Provider[F[_]] extends ValueSetProvider[Systems,F,Applicative[F]]
    {

      import cats.syntax.applicative._
      import cats.syntax.functor._

      val uri: URI =
        Coding.System[Systems].uri
    
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
      ): F[Option[ValueSet[Systems]]] =
        latest.map(Some(_))
    
      def latest(
        implicit env: Applicative[F]
      ): F[ValueSet[Systems]] =
        valueSet.pure

    }

  }


  object FamilyControlLevel
  extends CodedEnum("dnpm-dip/rd/diagnosis/family-control-level")
  with DefaultCodeSystem
  {
    val Single = Value("single-genome")
    val Duo    = Value("duo-genome")
    val Trio   = Value("trio-genome")

    override val display =
      Map(
        Single -> "Einzelgenom",
        Duo    -> "Duogenom",
        Trio   -> "Triogenom",  
      )

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }

  object VerificationStatus
  extends CodedEnum("dnpm-dip/rd/diagnosis/verification-status")
  with DefaultCodeSystem
  {

    val Unconfirmed = Value("unconfirmed")
    val Provisional = Value("provisional")
    val Partial     = Value("partial")
    val Confirmed   = Value("confirmed")

    override val display =
      Map(
        Confirmed   -> "Genetische Diagnose gesichert",
        Partial     -> "Klinischer Phänotyp nur partiell gelöst",
        Provisional -> "Genetische Verdachtsdiagnose",
        Unconfirmed -> "Keine genetische Diagnosestellung"
      )
    
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }


  object MissingCodeReason
  extends CodedEnum("dnpm-dip/rd/diagnosis/missing-code-reason")
  with DefaultCodeSystem
  {
    val NoMatchingCode = Value("no-matching-code")

    override val display =
      Map(
        NoMatchingCode -> "Kein geeigneter Code (ICD-10-GM, ORDO, Alpha-ID-SE) verfügbar",
      )

  }


  import de.dnpm.dip.util.json._

  implicit val format: OFormat[RDDiagnosis] =
    Json.format[RDDiagnosis]

}
