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


sealed trait OMIM

object OMIM extends CodeSystem.Publisher[OMIM]
{

  override implicit val codingSystem: Coding.System[OMIM] =
    Coding.System[OMIM]("https://www.omim.org")


  val filters: List[CodeSystem.Filter[OMIM]] =
    List.empty


  val EntryType =
    CodeSystem.Property[String](
      "kind",
      Some("Kind of ICD class")
    )


  override val properties: List[CodeSystem.Property] =
    List(
      EntryType
    )


  trait Catalog[F[_],Env] extends CodeSystemProvider[OMIM,F,Env]


  trait CatalogSPI extends SPIF[
    ({ type Service[F[_]] = Catalog[F,Applicative[F]] })#Service
  ]


  object Catalog extends SPILoaderF[CatalogSPI]

}
