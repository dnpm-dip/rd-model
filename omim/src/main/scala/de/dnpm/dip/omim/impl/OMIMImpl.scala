package de.dnpm.dip.omim.impl



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
import de.dnpm.dip.rd.model.OMIM



class OMIMCodeSystemProvider extends CodeSystemProviderSPI
{
  def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
    new OMIMImpl.Facade[F]
}


class OMIMCatalogProvider extends OMIM.CatalogSPI
{
  def getInstance[F[_]]: OMIM.Catalog[F,Applicative[F]] =
    new OMIMImpl.Facade[F]
}


object OMIMImpl
{

  trait Loader
  {
    def catalog: CodeSystem[OMIM]
  }

  trait LoaderSPI extends SPI[Loader]

  object Loader extends SPILoader[LoaderSPI]


  class DefaultLoader extends Loader
  {

    override val catalog: CodeSystem[OMIM] =
      CSVParser.read(
        this.getClass
          .getClassLoader
          .getResourceAsStream("mim2gene.txt")
      )
  }


  //TODO: Add Loader that automatically downloads the CSV OMIM file from external URL



  private val loader: Loader =
    Loader
      .getInstance
      .getOrElse(new DefaultLoader)



  private [impl] class Facade[F[_]] extends OMIM.Catalog[F,Applicative[F]]
  {

    import cats.syntax.functor._
    import cats.syntax.applicative._

    override val uri =
      Coding.System[OMIM].uri


    override val versionOrdering: Ordering[String] =
      Version.OrderedByDate


    override def versions(
      implicit F: Applicative[F]
    ): F[NonEmptyList[String]] =
      latest.map(cs => NonEmptyList.one(cs.version.get))

    override def latestVersion(
      implicit F: Applicative[F]
    ): F[String] =
      versions.map(_.head)

    override def filters(
      implicit env: Applicative[F] 
    ): F[List[CodeSystem.Filter[OMIM]]] =
      List.empty.pure


    override def get(
      version: String
    )(
      implicit F: Applicative[F]
    ): F[Option[CodeSystem[OMIM]]] =
      latest.map(Some(_))

    override def latest(
      implicit F: Applicative[F]
    ): F[CodeSystem[OMIM]] =
      loader.catalog.pure

  }


}
