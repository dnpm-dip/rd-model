package de.dnpm.dip.alphaidse.impl



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
import de.dnpm.dip.rd.model.AlphaIDSE



class AlphaIDSECodeSystemProvider extends CodeSystemProviderSPI
{
  def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
    new AlphaIDSEImpl.Facade[F]
}


class AlphaIDSECatalogsProvider extends AlphaIDSE.CatalogsSPI
{
  def getInstance[F[_]]: AlphaIDSE.Catalogs[F,Applicative[F]] =
    new AlphaIDSEImpl.Facade[F]
}


object AlphaIDSEImpl
{

  trait Loader
  {
    def catalogs: NonEmptyList[(String,CodeSystem[AlphaIDSE])]
  }

  trait LoaderSPI extends SPI[Loader]

  object Loader extends SPILoader[LoaderSPI]


  class DefaultLoader extends Loader
  {

    val catalogs: NonEmptyList[(String,CodeSystem[AlphaIDSE])] =
      NonEmptyList
        .of("2024","2025","2026")
        .map {
          version =>
            version -> CSVParser.read(
              this.getClass
                .getClassLoader
                .getResourceAsStream(s"icd10gm${version}_alphaidse.txt"),
                version
            )
        }
  }


  private lazy val catalogs =
    Loader
      .getInstance
      .getOrElse(new DefaultLoader)
      .catalogs


  private [impl] class Facade[F[_]] extends AlphaIDSE.Catalogs[F,Applicative[F]]
  {

    import cats.syntax.functor._
    import cats.syntax.applicative._

    override val uri =
      Coding.System[AlphaIDSE].uri


    override val versionOrdering: Ordering[String] =
      Version.OrderedByYear


    override def versions(
      implicit F: Applicative[F]
    ): F[NonEmptyList[String]] =
      catalogs.map(_._1)
        .pure

    override def latestVersion(
      implicit F: Applicative[F]
    ): F[String] =
      versions
        .map(_.toList.max(versionOrdering))

    override def filters(
      implicit env: Applicative[F] 
    ): F[List[CodeSystem.Filter[AlphaIDSE]]] =
      AlphaIDSE.filters
        .pure

    override def get(
      version: String
    )(
      implicit F: Applicative[F]
    ): F[Option[CodeSystem[AlphaIDSE]]] =
      catalogs
        .collectFirst { case (v,cs) if v == version => cs }
        .pure

    override def latest(
      implicit F: Applicative[F]
    ): F[CodeSystem[AlphaIDSE]] =
      catalogs
        .toList
        .maxBy(_._1)(versionOrdering)
        ._2
        .pure
  }

}
