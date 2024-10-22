package de.dnpm.dip.rd.model


import cats.Applicative
import de.dnpm.dip.coding.{
  Coding,
  Code,
  CodeSystem,
  CodeSystemProvider,
}
import de.dnpm.dip.util.{
  SPIF,
  SPILoaderF
}
import de.dnpm.dip.coding.icd.ICD10GM


sealed trait AlphaIDSE

object AlphaIDSE extends CodeSystem.Publisher[AlphaIDSE]
{

  override implicit val codingSystem: Coding.System[AlphaIDSE] =
    Coding.System[AlphaIDSE]("https://www.bfarm.de/DE/Kodiersysteme/Terminologien/Alpha-ID-SE")


  val filters: List[CodeSystem.Filter[AlphaIDSE]] =
    List.empty


  val Validity =
    CodeSystem.Property[String](
      name        = "validity",
      description = Some("Gültigkeit (0 - nicht gültig, 1 - gültig)"),
      valueSet    = None
    )

  val PrimaryCode1 =
    CodeSystem.Property[String](
      name        = "primary-code-1",
      description = Some("Primärschlüsselnummer 1 (ggf. mit Kreuz)"),
      valueSet    = None
    )

  val PrimaryCode2 =
    CodeSystem.Property[String](
      name        = "primary-code-2",
      description = Some("Primärschlüsselnummer 2 (ggf. mit Kreuz)"),
      valueSet    = None
    )

  val StarCode =
    CodeSystem.Property[String](
      name        = "star-code",
      description = Some("Sternschlüsselnummer (mit Stern)"),
      valueSet    = None
    )

  val AdditionalCode =
    CodeSystem.Property[String](
      name        = "additional-code",
      description = Some("Zusatzschlüsselnummer (mit Ausrufezeichen)"),
      valueSet    = None
    )

  val OrphaCode =
    CodeSystem.Property[String](
      name        = "orpha-code",
      description = Some("Orpha-Kennnummer (zurzeit 1- bis maximal 6-stellig)"),
      valueSet    = None
    )


  override val properties: List[CodeSystem.Property] =
    List(
      Validity,
      PrimaryCode1,
      PrimaryCode2,
      StarCode,
      AdditionalCode,
      OrphaCode
    )


  object extensions
  {

    implicit class AlphaIDProperties(val concept: CodeSystem.Concept[AlphaIDSE]) extends AnyVal
    {

      def isValid: Option[Boolean] =
        concept.get(Validity)
          .flatMap(_.headOption)
          .collect { 
            case "1" => true
            case _   => false
          }

      def primaryCode1: Option[Code[ICD10GM]] =
        concept.get(PrimaryCode1)
          .flatMap(_.headOption)
          .map(Code[ICD10GM](_))

      def primaryCode2: Option[Code[ICD10GM]] =
        concept.get(PrimaryCode2)
          .flatMap(_.headOption)
          .map(Code[ICD10GM](_))

      def starCode: Option[Code[ICD10GM]] =
        concept.get(StarCode)
          .flatMap(_.headOption)
          .map(Code[ICD10GM](_))

      def additionalCode: Option[Code[ICD10GM]] =
        concept.get(AdditionalCode)
          .flatMap(_.headOption)
          .map(Code[ICD10GM](_))

      def orphaCode: Option[Code[Orphanet]] =
        concept.get(OrphaCode)
          .flatMap(_.headOption)
          .map(Code[Orphanet](_))

    }

  }



  trait Catalogs[F[_],Env] extends CodeSystemProvider[AlphaIDSE,F,Env]

  trait CatalogsSPI extends SPIF[
    ({ type Service[F[_]] = Catalogs[F,Applicative[F]] })#Service
  ]


  object Catalogs extends SPILoaderF[CatalogsSPI]

}
