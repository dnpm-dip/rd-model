package de.dnpm.dip.rd.model


import cats.{
  Applicative,
  Id
}
import cats.data.NonEmptyList
import de.dnpm.dip.util.Completer
import de.dnpm.dip.model.{
  BaseCompleters,
  Patient,
  Observation,
//  Site
}
import de.dnpm.dip.coding.{
  Code,
  Coding,
  CodeSystem,
  CodeSystemProvider
}
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.coding.hgvs.HGVS
import de.dnpm.dip.coding.icd.ICD10GM
import de.dnpm.dip.rd.model.{
  ACMG,
  HPO,
  HPOTerm,
  OMIM,
  Orphanet,
  RDDiagnosis,
  RDNGSReport,
  RDPatientRecord,
  SmallVariant,
  CopyNumberVariant,
  StructuralVariant
}
/*
import shapeless.{
  Coproduct,
  :+:,
  CNil
}
*/


trait Completers extends BaseCompleters
{

  import Completer.syntax._
  import scala.util.chaining._ 


  protected implicit val hpOntology: CodeSystem[HPO]

  protected implicit val hgnc: CodeSystem[HGNC]

  protected implicit val ordo: CodeSystem[Orphanet]

  protected implicit val omim: CodeSystem[OMIM]

  protected implicit val icd10gm: CodeSystemProvider[ICD10GM,Id,Applicative[Id]]


  implicit val diagnosisCompleter: Completer[RDDiagnosis] =
    Completer.of(
      diag =>
        diag.copy(
          categories         = diag.categories.complete, 
          verificationStatus = diag.verificationStatus.complete
        )
    )


  implicit val hpoTermCompleter: Completer[HPOTerm] =
    Completer.of(
      hpo =>
        hpo.copy(
          value = hpo.value.complete
        )
    )


  implicit val acmgCriterionCompleter: Completer[ACMG.Criterion] =
    Completer.of(
      acmg =>
        acmg.copy(
          value    = acmg.value.complete,
          modifier = acmg.modifier.complete
        )
    )


  implicit val smallVariantCompleter: Completer[SmallVariant] =
    Completer.of(
      v =>
        v.copy(
          genes               = v.genes.complete,
          acmgClass           = v.acmgClass.complete,
          acmgCriteria        = v.acmgCriteria.complete,
          zygosity            = v.zygosity.complete,
          segregationAnalysis = v.segregationAnalysis.complete,
          modeOfInheritance   = v.modeOfInheritance.complete,
          significance        = v.significance.complete,
        )
    )


  implicit val structuralVariantCompleter: Completer[StructuralVariant] =
    Completer.of(
      v =>
        v.copy(
          genes               = v.genes.complete,
          acmgClass           = v.acmgClass.complete,
          acmgCriteria        = v.acmgCriteria.complete,
          zygosity            = v.zygosity.complete,
          segregationAnalysis = v.segregationAnalysis.complete,
          modeOfInheritance   = v.modeOfInheritance.complete,
          significance        = v.significance.complete,
        )
    )


  implicit val copyNumberVariantCompleter: Completer[CopyNumberVariant] =
    Completer.of(
      v =>
        v.copy(
          genes               = v.genes.complete,
          acmgClass           = v.acmgClass.complete,
          acmgCriteria        = v.acmgCriteria.complete,
          zygosity            = v.zygosity.complete,
          segregationAnalysis = v.segregationAnalysis.complete,
          modeOfInheritance   = v.modeOfInheritance.complete,
          significance        = v.significance.complete,
        )
    )


  implicit val ngsReportCompleter: Completer[RDNGSReport] =
    Completer.of(
      ngs =>
        ngs.copy(
          smallVariants      = ngs.smallVariants.complete,
          structuralVariants = ngs.structuralVariants.complete,
          copyNumberVariants = ngs.copyNumberVariants.complete
        )
    )


  implicit val rdPatientRecordCompleter: Completer[RDPatientRecord] =
    Completer.of(
      patRec =>
        patRec.copy(
          patient    = patRec.patient.complete,
          diagnosis  = patRec.diagnosis.complete,
          hpoTerms   = patRec.hpoTerms.complete,
          ngsReports = patRec.ngsReports.complete
        )
    )

}

object Completers extends Completers
{

  override implicit lazy val hpOntology: CodeSystem[HPO] =
    HPO.Ontology
      .getInstance[cats.Id]
      .get
      .latest

  override implicit lazy val hgnc: CodeSystem[HGNC] =
    HGNC.GeneSet
      .getInstance[cats.Id]
      .get
      .latest

  override implicit lazy val ordo: CodeSystem[Orphanet] =
    Orphanet.Ordo
      .getInstance[cats.Id]
      .get
      .latest

  override implicit lazy val omim: CodeSystem[OMIM] =
    OMIM.Catalog
      .getInstance[cats.Id]
      .get
      .latest

  override implicit lazy val icd10gm: CodeSystemProvider[ICD10GM,Id,Applicative[Id]] =
    ICD10GM.Catalogs
      .getInstance[cats.Id]
      .get

}
