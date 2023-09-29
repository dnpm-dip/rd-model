package de.dnpm.dip.rd.model


import cats.Applicative
import de.dnpm.dip.coding.{
  Coding,
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

  implicit val codingSystem: Coding.System[HPO] =
    Coding.System[HPO]("https://hpo.jax.org")


  val filters: List[CodeSystem.Filter[HPO]] =
    List.empty

  val properties: List[CodeSystem.Property] =
    List.empty


  trait Ontology[F[_],Env] extends CodeSystemProvider[HPO,F,Env]


  trait OntologySPI extends SPIF[
    ({ type Service[F[_]] = Ontology[F,Applicative[F]] })#Service
  ]


  object Ontology extends SPILoaderF[OntologySPI]

}
