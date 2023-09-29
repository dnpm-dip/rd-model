package de.dnpm.dip.rd.model



import cats.Applicative
import de.dnpm.dip.coding.{
  Coding,
  CodeSystem,
  CodeSystemProvider,
  CodeSystemProviderSPI,
  SingleCodeSystemProvider
}
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.model.{
  Id,
  ExternalId,
  Reference,
  Patient,
  Observation
}
import play.api.libs.json.{
  Json,
  Format
}



final case class HPOTerm
(
  id: Id[HPOTerm],
  patient: Reference[Patient],
  value: Coding[HPO]
)
extends Observation[Coding[HPO]]

object HPOTerm
{
   implicit val format: Format[HPOTerm] =
    Json.format[HPOTerm]
}



final case class Autozygosity
(
  id: Id[Autozygosity],
  patient: Reference[Patient],
  value: Float
)
extends Observation[Float]

object Autozygosity
{
   implicit val format: Format[Autozygosity] =
    Json.format[Autozygosity]
}




sealed trait HGVS
object HGVS
{
  implicit val hgvsSystem: Coding.System[HGVS] =
    Coding.System[HGVS]("https://varnomen.hgvs.org")
}


final case class Variant
(
  id: Id[Variant],
  patient: Reference[Patient],
  gene: Coding[HGNC],
  levelOfEvidence: Option[String],
  iscnDescription: Option[String],
  pubMedID: Option[ExternalId[Variant]],
  cDNAChange: Option[Coding[HGVS]],
  gDNAChange: Option[Coding[HGVS]],
  proteinChange: Option[Coding[HGVS]],
  acmgClass: Option[Coding[Variant.ACMGClass]],
  zygosity: Option[Coding[Variant.Zygosity]],
  deNovo: Option[Coding[Variant.DeNovo]],
  modeOfInheritance: Option[Coding[Variant.InheritanceMode]],
  significance: Option[Coding[Variant.Significance]],
  clinVarAccessionID: Option[Set[String]]
)

object Variant
{

  sealed trait ACMGClass
  object ACMGClass
  {
    implicit val acmgSystem: Coding.System[ACMGClass] =
      Coding.System[ACMGClass]("https://www.acmg.net")
   
    implicit val acmgCodeSystem: CodeSystem[ACMGClass] =
      CodeSystem[ACMGClass](
        name = "ACMG-Class",
        title = Some("ACMG-Class"),
        version = None,
        "pathogenic"        -> "Pathogenic",
        "likely-pathogenic" -> "Likely pathogenic",
        "unclear"           -> "Unclear",
        "likely-benign"     -> "Likely benign",
        "benign"            -> "Benign"
      )
  
    object Provider extends SingleCodeSystemProvider[ACMGClass](acmgCodeSystem)
  
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }

  sealed trait Zygosity
  object Zygosity
  {
    implicit val zygositySystem: Coding.System[Zygosity] =
      Coding.System[Zygosity]("dnpm-dip/rd/variant/zygosity")
  
    implicit val zygosityCodeSystem: CodeSystem[Zygosity] =
      CodeSystem[Zygosity](
        name = "Zygosity",
        title = Some("Zygosity"),
        version = None,
        "heterozygous"  -> "Heterozygous",
        "homozygous"    -> "Homozygous",
        "comp-het"      -> "Comp. het",
        "hemi"          -> "Hemizygous",
        "homoplasmic"   -> "Homoplasmic",
        "heteroplasmic" -> "Heteroplasmic"
      )
  
    object Provider extends SingleCodeSystemProvider(zygosityCodeSystem)
  
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }


  sealed trait DeNovo
  object DeNovo
  {
    implicit val deNovoSystem: Coding.System[DeNovo] =
      Coding.System[DeNovo]("dnpm-dip/rd/variant/de-novo")
  
    implicit val deNovoCodeSystem: CodeSystem[DeNovo] =
      CodeSystem[DeNovo](
        name = "de-novo",
        title = Some("de novo"),
        version = None,
        "yes" -> "Yes",
        "no" -> "No",
        "from-father" -> "Transmitted from father",
        "from-mother" -> "Transmitted from mother"
      )
  
    object Provider extends SingleCodeSystemProvider(deNovoCodeSystem)
  
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }
  

  sealed trait InheritanceMode
  object InheritanceMode
  {
    implicit val inhModeSystem: Coding.System[InheritanceMode] =
      Coding.System[InheritanceMode]("dnpm-dip/rd/variant/mode-of-inheritance")
  
    implicit val inhModeCodeSystem: CodeSystem[InheritanceMode] =
      CodeSystem[InheritanceMode](
        name = "mode-of-inheritance",
        title = Some("Mode of inheritance"),
        version = None,
        "dominant"      -> "Dominant",
        "recessive"     -> "Recessive",
        "X-linked"      -> "X-linked",
        "mitochondrial" -> "Mitochondrial",
        "unclear"       -> "Unclear"
      )
  
    object Provider extends SingleCodeSystemProvider(inhModeCodeSystem)
  
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }


  sealed trait Significance
  object Significance
  {
    implicit val significanceSystem: Coding.System[Significance] =
      Coding.System[Significance]("dnpm-dip/rd/variant/significance")
  
    implicit val significanceCodeSystem: CodeSystem[Significance] =
      CodeSystem[Significance](
        name = "variant-significance",
        title = Some("Variant Significance"),
        version = None,
        "primary" -> "Primary",
        "incidental" -> "Incidental",
        "candidate" -> "Candidate"
      )
  
    object Provider extends SingleCodeSystemProvider(significanceCodeSystem)
  
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }


  implicit val format: Format[Variant] =
    Json.format[Variant]
  
} // End object Variant
