package de.dnpm.dip.hpo.impl



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
import de.dnpm.dip.rd.model.HPO



class HPOCodeSystemProvider extends CodeSystemProviderSPI
{
  def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
    new HPOImpl.Facade[F]
}


class HPOntologyProvider extends HPO.OntologySPI
{
  def getInstance[F[_]]: HPO.Ontology[F,Applicative[F]] =
    new HPOImpl.Facade[F]
}


object HPOImpl
{

  trait OntologyLoader
  {
    def ontology: CodeSystem[HPO]
  }

  trait OntologyLoaderSPI extends SPI[OntologyLoader]

  object OntologyLoader extends SPILoader[OntologyLoaderSPI]


  class DefaultOntologyLoader extends OntologyLoader
  {

    val ontology: CodeSystem[HPO] =
      JsonParser.read(
        this.getClass
          .getClassLoader
          .getResourceAsStream("hp.json")
      )
  }


  //TODO: Add OntologyLoader that automatically downloads the JSON HPO from external URL



  private val ontologyLoader: OntologyLoader =
    OntologyLoader
      .getInstance
      .getOrElse(new DefaultOntologyLoader)



  private [impl] class Facade[F[_]] extends HPO.Ontology[F,Applicative[F]]
  {

    import cats.syntax.functor._
    import cats.syntax.applicative._

    override val uri =
      Coding.System[HPO].uri


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
    ): F[List[CodeSystem.Filter[HPO]]] =
      List.empty.pure


    override def get(
      version: String
    )(
      implicit F: Applicative[F]
    ): F[Option[CodeSystem[HPO]]] =
      latest.map(Some(_))

    override def latest(
      implicit F: Applicative[F]
    ): F[CodeSystem[HPO]] =
      ontologyLoader.ontology.pure

  }


}
