package de.dnpm.dip.rd.model


import cats.{
  Applicative,
  Id
}
import de.dnpm.dip.util.{
  Completer,
  DisplayLabel
}
import de.dnpm.dip.model.BaseCompleters
import de.dnpm.dip.coding.{
  Code,
  Coding,
  CodeSystem,
  CodeSystemProvider
}
import de.dnpm.dip.coding.atc.ATC
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.coding.icd.ICD10GM


trait Completers extends BaseCompleters
{

  import Completer.syntax._


  protected implicit val atc: CodeSystemProvider[ATC,Id,Applicative[Id]]

  protected implicit val hpOntology: CodeSystem[HPO]

  protected implicit val hgnc: CodeSystem[HGNC]

  protected implicit val ordo: CodeSystem[Orphanet]

  protected implicit val alphaId: CodeSystem[AlphaIDSE]

  protected implicit val icd10gm: CodeSystemProvider[ICD10GM,Id,Applicative[Id]]

  protected implicit val orphaCodingCompleter: Completer[Coding[Orphanet]] = {

    val addOrphaPrefix =
      (coding: Coding[Orphanet]) =>
        coding.code match { 
          case Code(c) if c startsWith "ORPHA:" => coding

          case Code(c) => coding.copy(code = Code[Orphanet](s"ORPHA:$c"))
        }

    addOrphaPrefix andThen Coding.completeByCodeSystem(ordo)
  }


  implicit val diagnosisCompleter: Completer[RDDiagnosis] =
    diag => diag.copy(
      familyControlLevel = diag.familyControlLevel.complete,
      verificationStatus = diag.verificationStatus.complete,
      codes              = diag.codes.complete, 
      missingCodeReason  = diag.missingCodeReason.complete,
    )


  implicit val hpoTermCompleter: Completer[HPOTerm] = {

    implicit val statusCompleter: Completer[HPOTerm.Status] =
      st => st.copy(
        status = st.status.complete 
      )

    hpo => hpo.copy(
      value = hpo.value.complete,
      status = hpo.status.complete
    )

  }


  implicit val gmfcsCompleter: Completer[GMFCSStatus] =
    gmfcs => gmfcs.copy(
      value = gmfcs.value.complete
    )


  implicit val hospitalizationCompleter: Completer[Hospitalization] =
    hosp => hosp.copy(
      numberOfStays = hosp.numberOfStays.complete,
      numberOfDays = hosp.numberOfDays.complete
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
      sequencingInfo = ngs.sequencingInfo.map(
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


  implicit def carePlanCompleter(
    implicit variants: List[Variant]
  ): Completer[RDCarePlan] = {

    implicit val therapyRecommendationCompleter: Completer[RDTherapyRecommendation] =
      rec => rec.copy(
        category   = rec.category.complete,
        `type`     = rec.`type`.complete,
        medication = rec.medication.complete, 
        supportingVariants =
          rec.supportingVariants
            .map(
              _.map(ref => ref.withDisplay(DisplayLabel.of(ref).value))
            )
      )

    implicit val studyRecommendationCompleter: Completer[RDStudyEnrollmentRecommendation] =
      rec => rec.copy(
        supportingVariants =
          rec.supportingVariants
            .map(
              _.map(ref => ref.withDisplay(DisplayLabel.of(ref).value))
            )
      )

    implicit val clinMgmtCompleter: Completer[ClinicalManagementRecommendation] =
      rec => rec.copy(
        `type` = rec.`type`.complete
      )

    cp => cp.copy(
      noSequencingPerformedReason      = cp.noSequencingPerformedReason.complete,
      therapyRecommendations           = cp.therapyRecommendations.complete,
      studyEnrollmentRecommendations   = cp.studyEnrollmentRecommendations.complete,
      clinicalManagementRecommendation = cp.clinicalManagementRecommendation.complete 
    )
  }


  implicit val therapyCompleter: Completer[RDTherapy] =
    th => th.copy(
      category   = th.category.complete,
      `type`     = th.`type`.complete,
      medication = th.medication.complete, 
    )


  implicit val rdPatientRecordCompleter: Completer[RDPatientRecord] = {
    patRec =>

      implicit val variants =
        patRec.ngsReports
          .getOrElse(List.empty)
          .flatMap(_.variants)

      patRec.copy(
        patient         = patRec.patient.complete,
        diagnoses       = patRec.diagnoses.complete,
        gmfcsStatus     = patRec.gmfcsStatus.complete,
        hospitalization = patRec.hospitalization.complete,
        hpoTerms        = patRec.hpoTerms.complete,
        ngsReports      = patRec.ngsReports.complete,
        carePlans       = patRec.carePlans.complete,
        followUps       = patRec.followUps.complete,
        therapies       = patRec.therapies.complete
      )
  }

}

object Completers extends Completers
{

  override implicit lazy val atc: CodeSystemProvider[ATC,Id,Applicative[Id]] =
    ATC.Catalogs
      .getInstance[cats.Id]
      .get

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
