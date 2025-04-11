package de.dnpm.dip.rd.model


import scala.util.chaining._
import cats.{
  Applicative,
  Id
}
import de.dnpm.dip.util.Completer
import de.dnpm.dip.model.BaseCompleters
import de.dnpm.dip.coding.{
  CodeSystem,
  CodeSystemProvider
}
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.coding.icd.ICD10GM


trait Completers extends BaseCompleters
{

  import Completer.syntax._


  protected implicit val hpOntology: CodeSystem[HPO]

  protected implicit val hgnc: CodeSystem[HGNC]

  protected implicit val ordo: CodeSystem[Orphanet]

  protected implicit val alphaId: CodeSystem[AlphaIDSE]

  protected implicit val icd10gm: CodeSystemProvider[ICD10GM,Id,Applicative[Id]]


  implicit val diagnosisCompleter: Completer[RDDiagnosis] =
    diag => diag.copy(
      codes              = diag.codes.complete, 
      verificationStatus = diag.verificationStatus.complete
    )


  implicit val hpoTermCompleter: Completer[HPOTerm] =
    hpo => hpo.copy(
      value = hpo.value.complete
    )


  implicit val acmgCriterionCompleter: Completer[ACMG.Criterion] =
    acmg => acmg.copy(
      value    = acmg.value.complete,
      modifier = acmg.modifier.complete
    )


  implicit val ngsReportCompleter: Completer[RDNGSReport] = {

    implicit val smallVariantCompleter: Completer[SmallVariant] =
      v => v.copy(
        genes               = v.genes.complete,
        acmgClass           = v.acmgClass.complete,
        acmgCriteria        = v.acmgCriteria.complete,
        zygosity            = v.zygosity.complete,
        segregationAnalysis = v.segregationAnalysis.complete,
        modeOfInheritance   = v.modeOfInheritance.complete,
        significance        = v.significance.complete,
      )
    
    
    implicit val structuralVariantCompleter: Completer[StructuralVariant] =
      v => v.copy(
        genes               = v.genes.complete,
        acmgClass           = v.acmgClass.complete,
        acmgCriteria        = v.acmgCriteria.complete,
        zygosity            = v.zygosity.complete,
        segregationAnalysis = v.segregationAnalysis.complete,
        modeOfInheritance   = v.modeOfInheritance.complete,
        significance        = v.significance.complete,
      )
    
    
    implicit val copyNumberVariantCompleter: Completer[CopyNumberVariant] =
      v => v.copy(
        genes               = v.genes.complete,
        acmgClass           = v.acmgClass.complete,
        acmgCriteria        = v.acmgCriteria.complete,
        zygosity            = v.zygosity.complete,
        segregationAnalysis = v.segregationAnalysis.complete,
        modeOfInheritance   = v.modeOfInheritance.complete,
        significance        = v.significance.complete,
      )

    ngs => ngs.copy(
      `type` = ngs.`type`.complete,
      sequencingInfo = ngs.sequencingInfo.pipe(
        seqInfo => seqInfo.copy(
          platform = seqInfo.platform.complete,
        )
      ),
      results = ngs.results.map(
        r => r.copy(
          smallVariants      = r.smallVariants.complete,
          structuralVariants = r.structuralVariants.complete,
          copyNumberVariants = r.copyNumberVariants.complete
        )
      )
    )
  }

  implicit val rdPatientRecordCompleter: Completer[RDPatientRecord] =
    patRec => patRec.copy(
      patient    = patRec.patient.complete,
      diagnoses  = patRec.diagnoses.complete,
      hpoTerms   = patRec.hpoTerms.complete,
      ngsReports = patRec.ngsReports.complete
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

  override implicit lazy val alphaId: CodeSystem[AlphaIDSE] =
    AlphaIDSE.Catalogs
      .getInstance[cats.Id]
      .get
      .latest

  override implicit lazy val icd10gm: CodeSystemProvider[ICD10GM,Id,Applicative[Id]] =
    ICD10GM.Catalogs
      .getInstance[cats.Id]
      .get

}
