package de.dnpm.dip.rd.model


import cats.Applicative
import de.dnpm.dip.coding.{
  Coding,
  Code,
  CodeSystem,
  CodeSystemProvider,
}
import de.dnpm.dip.coding.icd.{
  ICD10GM,
  ICD11
}
import de.dnpm.dip.util.{
  SPIF,
  SPILoaderF
}


sealed trait Orphanet

object Orphanet extends CodeSystem.Publisher[Orphanet]
{

  override implicit val codingSystem: Coding.System[Orphanet] =
    Coding.System[Orphanet]("https://www.orpha.net")


  val filters: List[CodeSystem.Filter[Orphanet]] =
    List.empty

  val SuperClasses =
    CodeSystem.Property[String](
      name = "SuperClasses",
      description = Some("Super-classes of the ORDO concept/class"),
      valueSet = None
    )

  val AlternativeTerms =
    CodeSystem.Property[String](
      name = "AlternativeTerms",
      description = Some("Alternative terms for the ORDO concept"),
      valueSet = None
    )

  val ICD10Codes =
    CodeSystem.Property[String](
      name = "ICD-10-Codes",
      description = Some("Corresponding ICD-10 codes of the ORDO concept"),
      valueSet = None
    )

  val ICD11Codes =
    CodeSystem.Property[String](
      name = "ICD-11-Codes",
      description = Some("Corresponding ICD-11 codes of the ORDO concept"),
      valueSet = None
    )


  override val properties: List[CodeSystem.Property] =
    List(
      SuperClasses,
      AlternativeTerms,      
      ICD10Codes,
      ICD11Codes
    )


  object extensions
  {

    implicit class OrphanetConceptProperties(val concept: CodeSystem.Concept[Orphanet]) extends AnyVal
    {

      def superClasses: Set[Code[Orphanet]] =
        concept.get(SuperClasses)
          .getOrElse(Set.empty)
          .map(Code[Orphanet](_))

      def alternativeTerms: Set[String] =
        concept.get(AlternativeTerms)
          .getOrElse(Set.empty)

      def icd10Codes: Set[Code[ICD10GM]] =
        concept.get(ICD10Codes)
          .getOrElse(Set.empty)
          .map(Code[ICD10GM](_))

      def icd11Codes: Set[Code[ICD11]] =
        concept.get(ICD11Codes)
          .getOrElse(Set.empty)
          .map(Code[ICD11](_))

    }

  }



  trait Ordo[F[_],Env] extends CodeSystemProvider[Orphanet,F,Env]


  trait OrdoSPI extends SPIF[
    ({ type Service[F[_]] = Ordo[F,Applicative[F]] })#Service
  ]


  object Ordo extends SPILoaderF[OrdoSPI]

}
