package de.dnpm.dip.rd.model


import cats.Applicative
import de.dnpm.dip.coding.{
  Coding,
  Code,
  CodeSystem,
  CodeSystemProvider,
}
import de.dnpm.dip.coding.icd.ICD10GM
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

/*
  val SuperClasses =
    CodeSystem.Property[String](
      name = "SuperClasses",
      description = Some("Super-classes of the ORDO concept/class"),
      valueSet = None
    )
*/

  val AlternativeTerms =
    CodeSystem.Property[String](
      name = "AlternativeTerms",
      description = Some("Alternative terms for the ORDO concept"),
      valueSet = None
    )

  val ICD10Code =
    CodeSystem.Property[String](
      name = "ICD-10-Code",
      description = Some("Corresponding ICD-10 code of the ORDO concept"),
      valueSet = None
    )


  override val properties: List[CodeSystem.Property] =
    List(
//      SuperClasses,
      AlternativeTerms,      
      ICD10Code
    )


  object extensions
  {

    implicit class OrphanetConceptProperties(val concept: CodeSystem.Concept[Orphanet]) extends AnyVal
    {
/*
      def superClasses: Set[Code[Orphanet]] =
        concept.get(SuperClasses)
          .getOrElse(Set.empty)
          .map(Code[Orphanet](_))
*/

      def alternativeTerms: Set[String] =
        concept.get(AlternativeTerms)
          .getOrElse(Set.empty)

      def icd10Code: Option[Code[ICD10GM]] =
        concept.get(ICD10Code)
          .flatMap(_.headOption)
          .map(Code[ICD10GM](_))
    }

  }



  trait Ordo[F[_],Env] extends CodeSystemProvider[Orphanet,F,Env]


  trait OrdoSPI extends SPIF[
    ({ type Service[F[_]] = Ordo[F,Applicative[F]] })#Service
  ]


  object Ordo extends SPILoaderF[OrdoSPI]

}
