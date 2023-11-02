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
  Format,
  OFormat
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
   implicit val format: OFormat[HPOTerm] =
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
   implicit val format: OFormat[Autozygosity] =
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
  acmgClass: Coding[Variant.ACMGClass],
  acmgCriteria: Option[Set[Coding[Variant.ACMGCriteria]]],
  zygosity: Coding[Variant.Zygosity],
  segregationAnalysis: Option[Coding[Variant.SegregationAnalysis]],
  modeOfInheritance: Option[Coding[Variant.InheritanceMode]],
  significance: Option[Coding[Variant.Significance]],
  clinVarAccessionID: Option[Set[String]]
)

object Variant
{

  sealed trait ACMGClass
  object ACMGClass
  {
    implicit val acmgClassSystem: Coding.System[ACMGClass] =
      Coding.System[ACMGClass]("https://www.acmg.net/class")
   
    implicit val acmgClassCodeSystem: CodeSystem[ACMGClass] =
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
  
    object Provider extends SingleCodeSystemProvider[ACMGClass]
  
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }

  sealed trait ACMGCriteria
  object ACMGCriteria
  {
    implicit val acmgCriteriaSystem: Coding.System[ACMGCriteria] =
      Coding.System[ACMGCriteria]("https://www.acmg.net/criteria")
   
    implicit val acmgCriteriaCodeSystem: CodeSystem[ACMGCriteria] =
      CodeSystem[ACMGCriteria](
        name = "ACMG-Criteria",
        title = Some("ACMG-Criteria"),
        version = None,
        Seq(
          "PVS1",
          "PS1",
          "PS2",
          "PS3",
          "PS4",
          "PM1",
          "PM2",
          "PM3",
          "PM4",
          "PM5",
          "PM6",
          "PP1",
          "PP2",
          "PP3",
          "PP4",
          "PP5",
          "BA1",
          "BS1",
          "BS2",
          "BS3",
          "BS4",
          "BP1",
          "BP2",
          "BP3",
          "BP4",
          "BP5",
          "BP6",
          "BP7"
        )
        .map(c => (c,c)): _*
    )
  
    object Provider extends SingleCodeSystemProvider[ACMGCriteria]
  
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
  
    object Provider extends SingleCodeSystemProvider[Zygosity]
  
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }


  sealed trait SegregationAnalysis
  object SegregationAnalysis
  {
    implicit val segregationAnalysisSystem: Coding.System[SegregationAnalysis] =
      Coding.System[SegregationAnalysis]("dnpm-dip/rd/variant/segregation-analysis")
  
    implicit val segregationAnalysisCodeSystem: CodeSystem[SegregationAnalysis] =
      CodeSystem[SegregationAnalysis](
        name = "segregation-analysis",
        title = Some("Segregation Analysis"),
        version = None,
        "not-performed" -> "not performed",
        "de-novo"       -> "de novo",
        "from-father"   -> "Transmitted from father",
        "from-mother"   -> "Transmitted from mother"
      )
  
    object Provider extends SingleCodeSystemProvider[SegregationAnalysis]
  
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
  
    object Provider extends SingleCodeSystemProvider[InheritanceMode]
  
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
  
    object Provider extends SingleCodeSystemProvider[Significance]
  
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }


  implicit val format: OFormat[Variant] =
    Json.format[Variant]
  
} // End object Variant
