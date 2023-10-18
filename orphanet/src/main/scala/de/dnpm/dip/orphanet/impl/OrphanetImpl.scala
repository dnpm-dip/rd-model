package de.dnpm.dip.orphanet.impl



import cats.Applicative
import cats.data.NonEmptyList
import de.dnpm.dip.util.{
  SPI,
  SPILoader
}
import de.dnpm.dip.coding.{
  Coding,
  CodeSystem,
  CodeSystemProvider,
  CodeSystemProviderSPI,
  Version
}
import de.dnpm.dip.rd.model.Orphanet



class OrphanetCodeSystemProvider extends CodeSystemProviderSPI
{
  def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
    new OrphanetImpl.Facade[F]
}


class OrdoProvider extends Orphanet.OrdoSPI
{
  def getInstance[F[_]]: Orphanet.Ordo[F,Applicative[F]] =
    new OrphanetImpl.Facade[F]
}


object OrphanetImpl
{

  trait OrdoLoader
  {
    def ontology: CodeSystem[Orphanet]
  }

  trait OrdoLoaderSPI extends SPI[OrdoLoader]

  object OrdoLoader extends SPILoader[OrdoLoaderSPI]


  class DefaultOrdoLoader extends OrdoLoader
  {

    val ontology: CodeSystem[Orphanet] =
      OwlOrdoParser.read(
        this.getClass
          .getClassLoader
          .getResourceAsStream("ORDO_de_4.3.owl")
      )
  }


  //TODO: Add OrdoLoader that automatically downloads the OWL/XML Ordo from external URL



  private val ontologyLoader: OrdoLoader =
    OrdoLoader
      .getInstance
      .getOrElse(new DefaultOrdoLoader)



  private [impl] class Facade[F[_]] extends Orphanet.Ordo[F,Applicative[F]]
  {

    import cats.syntax.functor._
    import cats.syntax.applicative._

    override val uri =
      Coding.System[Orphanet].uri


    override val versionOrdering: Ordering[String] =
      scala.math.Ordering.String


    override def versions(
      implicit F: Applicative[F]
    ): F[NonEmptyList[String]] =
      latest.map(cs => NonEmptyList.one(cs.version.get))

    override def latestVersion(
      implicit F: Applicative[F]
    ): F[String] =
      versions.map(_.head)

    override def get(
      version: String
    )(
      implicit F: Applicative[F]
    ): F[Option[CodeSystem[Orphanet]]] =
      latest.map(Some(_))

    override def latest(
      implicit F: Applicative[F]
    ): F[CodeSystem[Orphanet]] =
      ontologyLoader.ontology.pure

  }


}
