package de.dnpm.dip.rd.model



import cats.Applicative
import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  CodeSystem,
  DefaultCodeSystem,
  CodeSystemProvider,
  CodeSystemProviderSPI,
  SingleCodeSystemProvider
}
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.coding.hgvs.HGVS
import de.dnpm.dip.util.Displays
import de.dnpm.dip.model.{
  Id,
  ExternalId,
  Reference,
  Patient,
  Publication,
  Observation
}
import play.api.libs.json.{
  Json,
  Format,
  OFormat
}


object Chromosome
extends CodedEnum("chromosome")
with DefaultCodeSystem
{
  val chr1,
      chr2,
      chr3,
      chr4,
      chr5,
      chr6,
      chr7,
      chr8,
      chr9,
      chr10,
      chr11,
      chr12,
      chr13,
      chr14,
      chr15,
      chr16,
      chr17,
      chr18,
      chr19,
      chr20,
      chr21,
      chr22,
      chrX,
      chrY,
      chrMT = Value

  override val display = {
    case chr => chr.toString
  }

  implicit val format: Format[Chromosome.Value] =
    Json.formatEnum(this)
}


object ACMG
{

  sealed trait Class
  object Class
  extends CodedEnum("https://www.acmg.net/class")
  with DefaultCodeSystem
  {
    val One   = Value("1")
    val Two   = Value("2")
    val Three = Value("3")
    val Four  = Value("4")
    val Five  = Value("5")

    override val display =
      Map(
        Five  -> "Pathogenic",
        Four  -> "Likely pathogenic",
        Three -> "Unclear",
        Two   -> "Likely benign",
        One   -> "Benign"
      )
  
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }


  final case class Criterion
  (
    value: Coding[Criterion.Type.Value],
    modifier: Option[Coding[Criterion.Modifier.Value]]
  )

  object Criterion
  {

    sealed trait Type
    object Type
    extends CodedEnum("https://www.acmg.net/criteria/type")
    with DefaultCodeSystem
    {
     
      val PVS1,
          PS1, 
          PS2, 
          PS3,
          PS4,
          PM1,
          PM2, 
          PM3, 
          PM4, 
          PM5, 
          PM6, 
          PP1, 
          PP2, 
          PP3, 
          PP4, 
          PP5, 
          BA1, 
          BS1, 
          BS2, 
          BS3, 
          BS4, 
          BP1, 
          BP2, 
          BP3, 
          BP4, 
          BP5, 
          BP6, 
          BP7 = Value


      override val display = 
        Map(
PVS1 -> """Null variant (nonsense, frameshift, canonical +/-1 or 2 splice sites, initiation codon, single or multi-exon deletion) in a gene where loss of function (LOF) is a known mechanism of disease \n\nCaveats: \n- Beware of genes where LOF is not a known disease mechanism (e.g. GFAP, MYH7) \n- Use caution interpreting LOF variants at the extreme 3\' end of a gene \n- Use caution with splice variants that are predicted to lead to exon skipping but leave the remainder of the protein intact \n- Use caution in the presence of multiple transcripts""",
PS1 -> """Same amino acid change as a previously established pathogenic variant regardless of nucleotide change. \n\nExample: Val->Leu caused by either G>C or G>T in the same codon \nCaveat: Beware of changes that impact splicing rather than at the amino acid/protein level""",
PS2 -> """De novo (both maternity and paternity confirmed) in a patient with the disease and no family history. \n\nNote: Confirmation of paternity only is insufficient. Egg donation, surrogate motherhood, errors in embryo transfer, etc. can contribute to non-maternity.""",
PS3 -> """Well-established in vitro or in vivo functional studies supportive of a damaging effect on the gene or gene product. \n\nNote: Functional studies that have been validated and shown to be reproducible and robust in a clinical diagnostic laboratory setting are considered the most well-established.""",
PS4 -> """The prevalence of the variant in affected individuals is significantly increased compared to the prevalence in controls. \n\nNote 1: Relative risk (RR) or odds ratio (OR), as obtained from case-control studies, is >5.0 and the confidence interval around the estimate of RR or OR does not include 1.0. See manuscript for detailed guidance. \n\nNote 2: In instances of very rare variants where case-control studies may not reach statistical significance, the prior observation of the variant in multiple unrelated patients with the same phenotype, and its absence in controls, may be used as moderate level of evidence.""",
PM1 -> """Located in a mutational hot spot and/or critical and well-established functional domain (e.g. active site of an enzyme) without benign variation.""",
PM2 -> """Absent from controls (or at extremely low frequency if recessive) in Exome Sequencing Project, 1000 Genomes or ExAC. \n\nCaveat: Population data for indels may be poorly called by next generation sequencing.""",
PM3 -> """For recessive disorders, detected in trans with a pathogenic variant. \n\nNote: This requires testing of parents (or offspring) to determine phase.""",
PM4 -> """Protein length changes due to in-frame deletions/insertions in a non-repeat region or stop-loss variants.""",
PM5 -> """Novel missense change at an amino acid residue where a different missense change determined to be pathogenic has been seen before. \n\nExample: Arg156His is pathogenic; now you observe Arg156Cys. \nCaveat: Beware of changes that impact splicing rather than at the amino acid/protein level.""",
PM6 -> """Assumed de novo, but without confirmation of paternity and maternity.""",
PP1 -> """Co-segregation with disease in multiple affected family members in a gene definitively known to cause the disease. \n\nNote: May be used as stronger evidence with increasing segregation data.""",
PP2 -> """Missense variant in a gene that has a low rate of benign missense variation and where missense variants are a common mechanism of disease.""",
PP3 -> """Multiple lines of computational evidence support a deleterious effect on the gene or gene product (conservation, evolutionary, splicing impact, etc). \n\nCaveat: As many in silico algorithms use the same or very similar input for their predictions, each algorithm should not be counted as an independent criterion. PP3 can be used only once in any evaluation of a variant.""",
PP4 -> """Patient\'s phenotype or family history is highly specific for a disease with a single genetic etiology.""",
PP5 -> """Reputable source recently reports variant as pathogenic but the evidence is not available to the laboratory to perform an independent evaluation.""",
BA1 -> """Allele frequency is above 5% in Exome Sequencing Project, 1000 Genomes, or ExAC.""",
BS1 -> """Allele frequency is greater than expected for disorder.""",
BS2 -> """Observed in a healthy adult individual for a recessive (homozygous), dominant (heterozygous), or X-linked (hemizygous) disorder with full penetrance expected at an early age.""",
BS3 -> """Well-established in vitro or in vivo functional studies shows no damaging effect on protein function or splicing.""",
BS4 -> """Lack of segregation in affected members of a family. \n\nCaveat: The presence of phenocopies for common phenotypes (i.e. cancer, epilepsy) can mimic lack of segregation among affected individuals. Also, families may have more than one pathogenic variant contributing to an autosomal dominant disorder, further confounding an apparent lack of segregation.""",
BP1 -> """Missense variant in a gene for which primarily truncating variants are known to cause disease""",
BP2 -> """Observed in trans with a pathogenic variant for a fully penetrant dominant gene/disorder; or observed in cis with a pathogenic variant in any inheritance pattern.""",
BP3 -> """In-frame deletions/insertions in a repetitive region without a known function""",
BP4 -> """Multiple lines of computational evidence suggest no impact on gene or gene product (conservation, evolutionary, splicing impact, etc) \n\nCaveat: As many in silico algorithms use the same or very similar input for their predictions, each algorithm cannot be counted as an independent criterion. BP4 can be used only once in any evaluation of a variant.""",
BP5 -> """Variant found in a case with an alternate molecular basis for disease.""",
BP6 -> """Reputable source recently reports variant as benign but the evidence is not available to the laboratory to perform an independent evaluation.""",
BP7 -> """A synonymous (silent) variant for which splicing prediction algorithms predict no impact to the splice consensus sequence nor the creation of a new splice site AND the nucleotide is not highly conserved."""
      )


      final class ProviderSPI extends CodeSystemProviderSPI
      {
        override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
          new Provider.Facade[F]
      }
        
    }

    sealed trait Modifier
    object Modifier
    extends CodedEnum("https://www.acmg.net/criteria/modifier")
    with DefaultCodeSystem
    {
      val pvs,
          ps, 
          pm, 
          pp, 
          ba, 
          bs, 
          bp, 
          bm = Value

      override val display =
        Map(
          pvs -> "very strong pathogenic",
          ps  -> "strong pathogenic",
          pm  -> "medium pathogenic",
          pp  -> "supporting pathogenic",
          ba  -> "stand-alone benign",
          bs  -> "strong benign",
          bp  -> "supporting benign",
          bm  -> "medium benign"
        )
    
      final class ProviderSPI extends CodeSystemProviderSPI
      {
        override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
          new Provider.Facade[F]
      }
        
    }

    implicit val format: OFormat[Criterion] =
      Json.format[Criterion]

  }

}


sealed trait ClinVar
object ClinVar
{
  implicit val codingSystem: Coding.System[ClinVar] =
    Coding.System[ClinVar]("https://www.ncbi.nlm.nih.gov/clinvar/")
}

sealed trait ISCN
object ISCN
{
  implicit val codingSystem: Coding.System[ISCN] =
    Coding.System[ISCN]("https://iscn.karger.com/")
}


sealed abstract class Variant
{
  val id: Id[Variant]
  val patient: Reference[Patient]
  val genes: Option[Set[Coding[HGNC]]]
  val cDNAChange: Option[Coding[HGVS]]
  val gDNAChange: Option[Coding[HGVS]]
  val proteinChange: Option[Coding[HGVS]]
  val acmgClass: Coding[ACMG.Class.Value]
  val acmgCriteria: Option[Set[ACMG.Criterion]]
  val zygosity: Coding[Variant.Zygosity.Value]
  val segregationAnalysis: Option[Coding[Variant.SegregationAnalysis.Value]]
  val modeOfInheritance: Option[Coding[Variant.InheritanceMode.Value]]
  val significance: Option[Coding[Variant.Significance.Value]]
  val clinVarID: Option[ExternalId[Variant]]
  val publications: Option[Set[Reference[Publication]]]
}

object Variant
{

  sealed trait Zygosity
  object Zygosity
  extends CodedEnum("dnpm-dip/rd/variant/zygosity")
  with DefaultCodeSystem
  {
    val heterozygous,
        homozygous,
        hemi,  
        homoplasmic,
        heteroplasmic = Value

    val compHet = Value("comp-het")

    override val display =
      Map(
        heterozygous  -> "Heterozygous",
        homozygous    -> "Homozygous",
        compHet       -> "Compound heterozygous",
        hemi          -> "Hemizygous",
        homoplasmic   -> "Homoplasmic",
        heteroplasmic -> "Heteroplasmic"
      )
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }


  sealed trait SegregationAnalysis
  object SegregationAnalysis
  extends CodedEnum("dnpm-dip/rd/variant/segregation-analysis")
  with DefaultCodeSystem
  {
    val notPerformed = Value("not-performed")
    val deNovo       = Value("de-novo")
    val fromFather   = Value("from-father")
    val fromMother   = Value("from-mother")

    override val display =
      Map(
        notPerformed -> "not performed",
        deNovo       -> "de-novo",
        fromFather   -> "Transmitted from father",
        fromMother   -> "Transmitted from mother"
      )
    
    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }
  

  sealed trait InheritanceMode
  object InheritanceMode
  extends CodedEnum("dnpm-dip/rd/variant/mode-of-inheritance")
  with DefaultCodeSystem
  {
    val dominant,
        recessive,
        mitochondrial,
        unclear = Value

    val Xlinked = Value("X-linked")

    override val display =
      Map(
        dominant      -> "Dominant",
        recessive     -> "Recessive",
        Xlinked       -> "X-linked",
        mitochondrial -> "Mitochondrial",
        unclear       -> "Unclear"
      )


    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }


  sealed trait Significance
  object Significance
  extends CodedEnum("dnpm-dip/rd/variant/significance")
  with DefaultCodeSystem
  {
     val primary,
         incidental,
         candidate = Value

     override val display =
       Map(
        primary    -> "Variant in context of patient's disease",
        incidental -> "Incidental finding",
        candidate  -> "Candidate variant"
      )


    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }
      
  }



  implicit val displays: Displays[Variant] =
    Displays {
      case sv: SmallVariant =>
        s"SmallVariant ${sv.genes.getOrElse(Set.empty).flatMap(_.display).mkString(",")} ${sv.proteinChange.orElse(sv.gDNAChange).orElse(sv.cDNAChange).map(_.display).getOrElse("")}"

      case sv: StructuralVariant =>
        s"StructuralVariant ${sv.genes.getOrElse(Set.empty).flatMap(_.display).mkString(",")} ${sv.proteinChange.orElse(sv.gDNAChange).orElse(sv.cDNAChange).map(_.display).getOrElse("")}"

      case cnv: CopyNumberVariant =>
        s"CNV ${cnv.genes.getOrElse(Set.empty).flatMap(_.display).mkString(",")} ${cnv.`type`.display.getOrElse("")} ${cnv.proteinChange.orElse(cnv.gDNAChange).orElse(cnv.cDNAChange).map(_.display).getOrElse("")}"
    }

} // End object Variant



final case class SmallVariant
(
  id: Id[Variant],
  patient: Reference[Patient],
  chromosome: Coding[Chromosome.Value],
  genes: Option[Set[Coding[HGNC]]],
  position: Int,
  ref: String,
  alt: String,
  cDNAChange: Option[Coding[HGVS]],
  gDNAChange: Option[Coding[HGVS]],
  proteinChange: Option[Coding[HGVS]],
  acmgClass: Coding[ACMG.Class.Value],
  acmgCriteria: Option[Set[ACMG.Criterion]],
  zygosity: Coding[Variant.Zygosity.Value],
  segregationAnalysis: Option[Coding[Variant.SegregationAnalysis.Value]],
  modeOfInheritance: Option[Coding[Variant.InheritanceMode.Value]],
  significance: Option[Coding[Variant.Significance.Value]],
  clinVarID: Option[ExternalId[Variant]],
  publications: Option[Set[Reference[Publication]]]
)
extends Variant

object SmallVariant
{
  implicit val format: OFormat[SmallVariant] =
    Json.format[SmallVariant]
}


final case class StructuralVariant
(
  id: Id[Variant],
  patient: Reference[Patient],
  genes: Option[Set[Coding[HGNC]]],
  iscnDescription: Option[Coding[ISCN]],
  cDNAChange: Option[Coding[HGVS]],
  gDNAChange: Option[Coding[HGVS]],
  proteinChange: Option[Coding[HGVS]],
  acmgClass: Coding[ACMG.Class.Value],
  acmgCriteria: Option[Set[ACMG.Criterion]],
  zygosity: Coding[Variant.Zygosity.Value],
  segregationAnalysis: Option[Coding[Variant.SegregationAnalysis.Value]],
  modeOfInheritance: Option[Coding[Variant.InheritanceMode.Value]],
  significance: Option[Coding[Variant.Significance.Value]],
  clinVarID: Option[ExternalId[Variant]],
  publications: Option[Set[Reference[Publication]]]
)
extends Variant

object StructuralVariant
{
  implicit val format: OFormat[StructuralVariant] =
    Json.format[StructuralVariant]
}


final case class CopyNumberVariant
(
  id: Id[Variant],
  patient: Reference[Patient],
  chromosome: Coding[Chromosome.Value],
  genes: Option[Set[Coding[HGNC]]],
  startPosition: Int,
  endPosition: Int,
  `type`: Coding[CopyNumberVariant.Type.Value],
  cDNAChange: Option[Coding[HGVS]],
  gDNAChange: Option[Coding[HGVS]],
  proteinChange: Option[Coding[HGVS]],
  acmgClass: Coding[ACMG.Class.Value],
  acmgCriteria: Option[Set[ACMG.Criterion]],
  zygosity: Coding[Variant.Zygosity.Value],
  segregationAnalysis: Option[Coding[Variant.SegregationAnalysis.Value]],
  modeOfInheritance: Option[Coding[Variant.InheritanceMode.Value]],
  significance: Option[Coding[Variant.Significance.Value]],
  clinVarID: Option[ExternalId[Variant]],
  publications: Option[Set[Reference[Publication]]]
)
extends Variant

object CopyNumberVariant
{ 
  sealed trait Type
  object Type
  extends CodedEnum("dnpm-dip/rd/cnv/type")
  with DefaultCodeSystem
  {
    val gain, loss = Value

    override val display =
      Map(
        gain -> "Gain",
        loss -> "Loss"
      )

    final class ProviderSPI extends CodeSystemProviderSPI
    {
      override def getInstance[F[_]]: CodeSystemProvider[Any,F,Applicative[F]] =
        new Provider.Facade[F]
    }

  }

  implicit val format: OFormat[CopyNumberVariant] =
    Json.format[CopyNumberVariant]
}
