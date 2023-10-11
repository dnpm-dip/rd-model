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


sealed trait HPO

object HPO extends CodeSystem.Publisher[HPO]
{

  override implicit val codingSystem: Coding.System[HPO] =
    Coding.System[HPO]("https://hpo.jax.org")


  val filters: List[CodeSystem.Filter[HPO]] =
    List.empty


  val Type =
    CodeSystem.Property[String](
      name        = "type",
      description = Some("Node type"),
      valueSet    = None
    )

  val SuperClasses =
    CodeSystem.Property[String](
      name        = "superClasses",
      description = Some("Super-classes, i.e. parent concepts"),
      valueSet    = None  //TODO: consider making this explicit with an Enumeration for possible values
    )

  val Definition =
    CodeSystem.Property[String](
      name        = "definition",
      description = Some("Definition of the concept, according to field 'meta / definition / val'"),
      valueSet    = None
    )

/*  
  val Synonyms =
    CodeSystem.Property[String](
      name        = "synonyms",
      description = Some("Synonyms for the concept, i.e. alternatives for the main label in field 'lbl'"),
      valueSet    = None
    )
*/


  override val properties: List[CodeSystem.Property] =
    List(
      Type,
      Definition,
      SuperClasses,
//      Synonyms
    )


  object extensions
  {

    implicit class HPOConceptProperties(val concept: CodeSystem.Concept[HPO]) extends AnyVal
    {

      def getType: Option[String] =
        concept.get(Type)
          .flatMap(_.headOption)

      def definition: Option[String] =
        concept.get(Definition)
          .flatMap(_.headOption)

      def superClasses: Set[Code[HPO]] =
        concept.get(SuperClasses)
          .getOrElse(Set.empty)
          .map(Code[HPO](_))

    }

  }



  trait Ontology[F[_],Env] extends CodeSystemProvider[HPO,F,Env]


  trait OntologySPI extends SPIF[
    ({ type Service[F[_]] = Ontology[F,Applicative[F]] })#Service
  ]


  object Ontology extends SPILoaderF[OntologySPI]

}
