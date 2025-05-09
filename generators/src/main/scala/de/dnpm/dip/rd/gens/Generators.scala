package de.dnpm.dip.rd.gens


import java.time.{
  LocalDate,
  YearMonth
}
import java.time.temporal.ChronoUnit.YEARS
import cats.data.NonEmptyList
import shapeless.Coproduct
import de.ekut.tbi.generators.Gen
import de.ekut.tbi.generators.DateTimeGens._
import de.dnpm.dip.coding.{
  Code,
  Coding,
  CodeSystem,
}
import de.dnpm.dip.coding.icd.ICD10GM
import de.dnpm.dip.coding.atc.ATC
import de.dnpm.dip.coding.atc.Kinds.Substance
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.coding.hgvs.HGVS
import de.dnpm.dip.model.{
  Id,
  Address,
  BaseVariant,
  ExternalId,
  FollowUp,
  Reference,
  Gender,
  GeneAlterationReference,
  HealthInsurance,
  History,
  IK,
  NGSReport,
  Patient,
  Period,
  Publication,
  PubMed,
  Study,
}
import de.dnpm.dip.rd.model._
import AlphaIDSE.extensions._


trait Generators
{

  implicit def genId[T]: Gen[Id[T]] =
    Gen.uuidStrings
      .map(Id(_))


  implicit def genExternalId[T,S: Coding.System]: Gen[ExternalId[T,S]] =
    Gen.uuidStrings
       .map(ExternalId[T,S](_))


  implicit def genExternalIdSysUnion[T,S <: Coproduct](
    implicit uris: Coding.System.UriSet[S]
  ): Gen[ExternalId[T,S]] =
    for {
      sys <- Gen.oneOf(uris.values.toSeq)
      id  <- Gen.uuidStrings
    } yield ExternalId[T,S](id,sys)



  implicit def genReference[T]: Gen[Reference[T]] =
    Gen.of[Id[T]]
      .map(Reference(_))


  implicit def genCodingfromCodeSystem[S: Coding.System: CodeSystem]: Gen[Coding[S]] =
    Gen.oneOf(CodeSystem[S].concepts)
      .map(Coding.fromConcept(_))


  implicit val genHpoCoding: Gen[Coding[HPO]] =
    Gen.oneOf(
      "HP:0100861",
      "HP:0100863",
      "HP:0100864",
      "HP:0100869",
      "HP:0100871",
      "HP:0100877"
    )
    .map(Coding[HPO](_))


  private implicit lazy val alphaIdSE: CodeSystem[AlphaIDSE] = 
    AlphaIDSE.Catalogs
      .getInstance[cats.Id]
      .get
      .latest
      .filter(
        c => c.isValid.exists(_ == true) && c.primaryCode1.isDefined && c.orphaCode.isDefined
      )
  

  private lazy val ordo: CodeSystem[Orphanet] =
    Orphanet.Ordo
      .getInstance[cats.Id]
      .get
      .latest

  private lazy val icd10gm: CodeSystem[ICD10GM] =
    ICD10GM.Catalogs
      .getInstance[cats.Id]
      .get
      .latest


  private val atcRegex =
    """B06AC|C09X|C10A""".r.unanchored

  private implicit lazy val atc: CodeSystem[ATC] =
    ATC.Catalogs
      .getInstance[cats.Id]
      .get
      .latest
      .filter(c => atcRegex matches c.code.value)
      .filter(ATC.filterByKind(Substance))


  implicit val genPubMedId: Gen[Reference[Publication]] =
    Gen.positiveInts
      .map(_.toString)
      .map(ExternalId[Publication,PubMed](_))
      .map(Reference(_))

  private val genGender: Gen[Coding[Gender.Value]] =
    Gen.distribution(
      48.0 -> Gender.Male,
      48.0 -> Gender.Female,
      4.0  -> Gender.Other,
    )
    .map(Coding(_))


  implicit val genPatient: Gen[Patient] =
    for {
      id <- Gen.of[Id[Patient]]

      gender <- genGender

      birthDate <-
        localDatesBetween(
          LocalDate.now.minusYears(70),
          LocalDate.now.minusYears(30)
        )

      age = YEARS.between(birthDate,LocalDate.now)

      dateOfDeath <-
        Gen.option(
          Gen.longsBetween(age - 20L, age - 5L)
            .map(birthDate.plusYears),
          0.4
        )

      healthInsurance =
        Patient.Insurance(
          Coding(HealthInsurance.Type.GKV),
          Some(
            Reference(ExternalId[HealthInsurance,IK]("1234567890"))
              .withDisplay("AOK")
            )
        )

    } yield Patient(
      id,
      gender,
      birthDate,
      dateOfDeath,
      None,
      healthInsurance,
      Address("12345")
    )

  def genDiagnosis(
    patient: Patient,
  ): Gen[RDDiagnosis] =
    for {
      id <- Gen.of[Id[RDDiagnosis]]

      date = LocalDate.now

      onsetMonth <-
        Gen.longsBetween(6,12)
          .map(date.minusMonths)
          .map(YearMonth.from)

      controlLevel <- Gen.of[Coding[RDDiagnosis.FamilyControlLevel.Value]]

      status <- Gen.of[Coding[RDDiagnosis.VerificationStatus.Value]]

      codes <-
         for {
           alpha <- Gen.oneOf(alphaIdSE.concepts)
           orpha = alpha.orphaCode.flatMap(ordo.concept)
           icd10 = alpha.primaryCode1.flatMap(icd10gm.concept)
         } yield
           NonEmptyList.of(alpha.toCodingOf[RDDiagnosis.Systems]) ++
             orpha.map(_.toCodingOf[RDDiagnosis.Systems]).toList ++
             icd10.map(_.toCodingOf[RDDiagnosis.Systems]).toList

      missingCode =
        Option.when(codes.size != 3)(Coding(RDDiagnosis.MissingCodeReason.NoMatchingCode))

    } yield
      RDDiagnosis(
        id,
        Reference.to(patient),
        date,
        onsetMonth,
        controlLevel,
        status,
        codes,
        missingCode,
        Some(List("Notes on the disease..."))
      )


  def genEpisode(
    patient: Patient,
  ): Gen[RDEpisodeOfCare] =
    for {
      id    <- Gen.of[Id[RDEpisodeOfCare]]
    } yield
      RDEpisodeOfCare(
        id,
        Reference.to(patient),
        Period(LocalDate.now)
      )


  def genHPOTerm(
    patient: Patient
  ): Gen[HPOTerm] =
    for {
      id     <- Gen.of[Id[HPOTerm]]

      date   = LocalDate.now.minusMonths(2)

      value  <- Gen.of[Coding[HPO]]

      status <- Gen.of[Coding[HPOTerm.Status.Value]]

    } yield
      HPOTerm(
        id,
        Reference.to(patient),
        Some(date),
        Some(YearMonth.now.minusMonths(6)),
        value,
        Some(
          History(
            HPOTerm.Status(
              status,
              LocalDate.now
            )
          )
        )
      )


  def genAutozygosity(
    patient: Patient
  ): Gen[Autozygosity] =
    for {
      id    <- Gen.of[Id[Autozygosity]]
      value <- Gen.floats
    } yield
      Autozygosity(
        id,
        Reference.to(patient),
        value
      )


  implicit val genHGNCCoding: Gen[Coding[HGNC]] =
    Gen.oneOf(
      Seq(
        "HGNC:20" -> "AARS1",
        "HGNC:51526" -> "AATBC",
        "HGNC:49667" -> "ABALON",
        "HGNC:17929" -> "AADAT",
        "HGNC:21" -> "AATK",
      )
      .map {
        case (code,display) =>
          Coding[HGNC](code,display)
      }
    )

  // Source: https://varnomen.hgvs.org/recommendations/DNA/variant/duplication/
  private val gDNAChanges =
    Seq(
      "NC_000023.10:g.33038255C>A",
      "NC_000023.11:g.32343183dup",
      "NC_000023.11:g.(31060227_31100351)_(33274278_33417151)dup",
      "NC_000023.11:g.pter_qtersup",
      "NC_000023.11:g.33344590_33344592=/dup",
    )
    .map(Code[HGVS.DNA](_))
//    .map(Coding[HGVS.DNA](_))

  private val proteinChanges =
    Seq(
      "LRG_199p1:p.Trp24Cys",
      "LRG_199p1:p.Trp24Ter (p.Trp24*)",
      "NP_003997.1:p.(Gly56Ala^Ser^Cys)",
      "LRG_199p1:p.Trp24=/Cys",
      "NP_003997.2:p.Val7dup",
    )
    .map(Code[HGVS.Protein](_))
//    .map(Coding[HGVS.Protein](_))


  implicit val genAcmgCriterion: Gen[ACMG.Criterion] =
    for { 
      value    <- Gen.of[Coding[ACMG.Criterion.Type.Value]]
      modifier <- Gen.of[Coding[ACMG.Criterion.Modifier.Value]]
    } yield ACMG.Criterion(
      value,
      Some(modifier)
    )

  private val bases =
    Seq("A","C","G","T")


  def genSmallVariant(
    patient: Patient
  ): Gen[SmallVariant] =
    for {
      id    <- Gen.of[Id[SmallVariant]]
      chr   <- Gen.`enum`(Chromosome)
      gene  <- Gen.of[Coding[HGNC]]
      localization <- Gen.of[Coding[BaseVariant.Localization.Value]]
      pos   <- Gen.positiveInts
      ref   <- Gen.oneOf(bases)
      alt   <- Gen.oneOf((bases.toSet - ref).toSeq)
      dnaChg <- Gen.oneOf(gDNAChanges)
      proteinChg <- Gen.oneOf(proteinChanges)
      acmgClass <- Gen.of[Coding[ACMG.Class.Value]]
      acmgCriteria <-
        Gen.list(
          Gen.intsBetween(1,3),
          Gen.of[ACMG.Criterion]
        )
      zygosity <- Gen.of[Coding[Variant.Zygosity.Value]]
      segAnalysis <- Gen.of[Coding[Variant.SegregationAnalysis.Value]]
      inh <- Gen.of[Coding[Variant.ModeOfInheritance.Value]]
      signif <- Gen.of[Coding[Variant.Significance.Value]]
      clinVarId <- genExternalId[SmallVariant,ClinVar]
      pmid <- Gen.of[Reference[Publication]]
    } yield 
      SmallVariant(
        id,
        Reference.to(patient),
        chr,
        Some(Set(gene)),
        Some(Set(localization)),
        pos,
        ref,
        alt,
        Some(dnaChg),
        Some(dnaChg),
        Some(proteinChg),
        acmgClass,
        Some(acmgCriteria.toSet),
        zygosity,
        Some(segAnalysis),
        Some(inh),
        Some(signif),
        Some(List(clinVarId)),
        Some(Set(pmid))
      )

  def genStructuralVariant(
    patient: Patient
  ): Gen[StructuralVariant] =
    for {
      id    <- Gen.of[Id[SmallVariant]]
      gene  <- Gen.of[Coding[HGNC]]
      localization <- Gen.of[Coding[BaseVariant.Localization.Value]]
      iscn  = Code[ISCN]("ISCN description...")
      dnaChg <- Gen.oneOf(gDNAChanges)
      proteinChg <- Gen.oneOf(proteinChanges)
      acmgClass <- Gen.of[Coding[ACMG.Class.Value]]
      acmgCriteria <-
        Gen.list(
          Gen.intsBetween(1,3),
          Gen.of[ACMG.Criterion]
        )
      zygosity <- Gen.of[Coding[Variant.Zygosity.Value]]
      segAnalysis <- Gen.of[Coding[Variant.SegregationAnalysis.Value]]
      inh <- Gen.of[Coding[Variant.ModeOfInheritance.Value]]
      signif <- Gen.of[Coding[Variant.Significance.Value]]
      clinVarId <- genExternalId[SmallVariant,ClinVar]
      pmid <- Gen.of[Reference[Publication]]
    } yield 
      StructuralVariant(
        id,
        Reference.to(patient),
        Some(Set(gene)),
        Some(Set(localization)),
        Some(iscn),
        Some(dnaChg),
        Some(dnaChg),
        Some(proteinChg),
        acmgClass,
        Some(acmgCriteria.toSet),
        zygosity,
        Some(segAnalysis),
        Some(inh),
        Some(signif),
        Some(List(clinVarId)),
        Some(Set(pmid))
      )

  def genCopyNumberVariant(
    patient: Patient
  ): Gen[CopyNumberVariant] =
    for {
      id    <- Gen.of[Id[SmallVariant]]
      chr   <- Gen.`enum`(Chromosome)
      gene  <- Gen.of[Coding[HGNC]]
      localization <- Gen.of[Coding[BaseVariant.Localization.Value]]
      start <- Gen.positiveInts
      end   <- Gen.positiveInts
      typ   <- Gen.of[Coding[CopyNumberVariant.Type.Value]]
      dnaChg <- Gen.oneOf(gDNAChanges)
      proteinChg <- Gen.oneOf(proteinChanges)
      acmgClass <- Gen.of[Coding[ACMG.Class.Value]]
      acmgCriteria <-
        Gen.list(
          Gen.intsBetween(1,3),
          Gen.of[ACMG.Criterion]
        )
      zygosity <- Gen.of[Coding[Variant.Zygosity.Value]]
      segAnalysis <- Gen.of[Coding[Variant.SegregationAnalysis.Value]]
      inh <- Gen.of[Coding[Variant.ModeOfInheritance.Value]]
      signif <- Gen.of[Coding[Variant.Significance.Value]]
      clinVarId <- genExternalId[SmallVariant,ClinVar]
      pmid <- Gen.of[Reference[Publication]]
    } yield 
      CopyNumberVariant(
        id,
        Reference.to(patient),
        chr,
        Some(Set(gene)),
        Some(Set(localization)),
        start,
        end,
        typ,
        Some(dnaChg),
        Some(dnaChg),
        Some(proteinChg),
        acmgClass,
        Some(acmgCriteria.toSet),
        zygosity,
        Some(segAnalysis),
        Some(inh),
        Some(signif),
        Some(List(clinVarId)),
        Some(Set(pmid))
      )



  def genNGSReport(
    patient: Patient
  ): Gen[RDNGSReport] =
    for {
      id  <- Gen.of[Id[RDNGSReport]]
      typ <- Gen.of[Coding[NGSReport.Type.Value]]

      metaInfo <-
        for {
          platform <- Gen.of[Coding[NGSReport.Platform.Value]]
        } yield RDNGSReport.SequencingInfo(
          platform,
          "Kit..."
        )
      autoZyg <- genAutozygosity(patient)
      conclusion <- Gen.of[Coding[RDNGSReport.Conclusion.Value]]
      smallVariants <- Gen.list(Gen.intsBetween(3,5),genSmallVariant(patient))
      cnvs <- Gen.list(Gen.intsBetween(3,5),genCopyNumberVariant(patient))
      svs <- Gen.list(Gen.intsBetween(3,5),genStructuralVariant(patient))
    } yield
      RDNGSReport(
        id,
        Reference.to(patient),
        LocalDate.now,
        typ,
        Some(metaInfo),
        Some(conclusion),
        Some(
          RDNGSReport.Results(
            Some(autoZyg),
            Some(smallVariants),
            Some(cnvs),
            Some(svs)
          )
        )
      )


  def genTherapyRecommendation(
    patient: Patient,
    variants: Seq[Variant]
  ): Gen[RDTherapyRecommendation] =
    for {
      id <- Gen.of[Id[RDTherapyRecommendation]]
      category <- Gen.of[Coding[RDTherapy.Category.Value]]
      typ  <- Gen.of[Coding[RDTherapy.Type.Value]]
      medication <- Gen.of[Coding[ATC]]
      supportingVariant <- Gen.oneOf(variants)
    } yield RDTherapyRecommendation(
      id,
      Reference.to(patient),
      LocalDate.now,
      category,
      typ,
      Some(Set(medication)),
      Some(
        List(GeneAlterationReference.to(supportingVariant))
      )
    )


  def genCarePlan(
    patient: Patient,
    variants: Seq[Variant]
  ): Gen[RDCarePlan] =
    for {
      id <- Gen.of[Id[RDCarePlan]]

      protocol = "Protocol of the RD conference..."

      recommendation <-
        genTherapyRecommendation(
          patient,
          variants
        )

      statusReason <- Gen.of[Coding[RDCarePlan.StatusReason.Value]]

      studyEnrollmentRecommendation <-
        for {
          stId <- Gen.of[Id[RDStudyEnrollmentRecommendation]]
          studyRef <- Gen.of[ExternalId[Study,Study.Registries]].map(Reference(_))
        } yield RDStudyEnrollmentRecommendation(
          stId,
          Reference.to(patient),
          LocalDate.now,
          recommendation.supportingVariants,
          NonEmptyList.of(studyRef)
        )

      clinicalManagementRecommendation <-
        for {
          recId <- Gen.of[Id[ClinicalManagementRecommendation]]
          typ <- Gen.of[Coding[ClinicalManagementRecommendation.Type.Value]]
        } yield ClinicalManagementRecommendation(
          recId,
          Reference.to(patient),
          LocalDate.now,
          typ,
          Some(List("Description of clinical management..."))
        )

    } yield RDCarePlan(
      id,
      Reference.to(patient),
      LocalDate.now,
      Some(statusReason),
      Some(true),
      Some(true),
      Some(List(recommendation)),
      Some(List(studyEnrollmentRecommendation)),
      Some(clinicalManagementRecommendation),
      Some(List(protocol))
    )


  def genTherapy(
    patient: Patient,
    recommendation: RDTherapyRecommendation
  ): Gen[History[RDTherapy]] =
    for { 
      id <- Gen.of[Id[RDTherapy]]
      notes = "Notes on the therapy..."
    } yield History(
      RDTherapy(
        id,
        Reference.to(patient),
        Some(Reference.to(recommendation)),
        LocalDate.now,
        Some(recommendation.category),
        Some(recommendation.`type`),
        recommendation.medication,
        Some(Period(recommendation.issuedOn)),
        Some(List(notes))
      )
    )


  def genGMFCS(
    patient: Patient
  ): Gen[GMFCSStatus] =
    for { 
      id <- Gen.of[Id[GMFCSStatus]]
      value <- Gen.of[Coding[GMFCS.Value]]
    } yield GMFCSStatus(
      id,
      Reference.to(patient),
      LocalDate.now,
      value
    )

  implicit val genHospitalization: Gen[Hospitalization] =
    for { 
      stays <- Gen.of[Coding[Hospitalization.NumberOfStays.Value]]
      days <- Gen.of[Coding[Hospitalization.NumberOfDays.Value]]
    } yield Hospitalization(
      stays,
      days
    )

  implicit val genRDPatientRecord: Gen[RDPatientRecord] =
    for {
      patient <-
        Gen.of[Patient]

      patRef = Reference.to(patient)

      diag <- genDiagnosis(patient)

      episode <- genEpisode(patient)

      hpoTerms <-
        Gen.list(
          Gen.intsBetween(3,5),
          genHPOTerm(patient)
        )
        .map(_.distinctBy(_.value.code))

      hospitalization <-  Gen.of[Hospitalization]

      gmfcsStatus <-
        genGMFCS(patient)

      ngsReport <-
        genNGSReport(patient)

      initBoardPlan <-
        for { 
          id <- Gen.of[Id[RDCarePlan]]
        } yield RDCarePlan(
          id,
          patRef,
          LocalDate.now.minusWeeks(2),
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )

      carePlan <-
        genCarePlan(
          patient,
          ngsReport.variants
        ) 

      followUp =
        FollowUp(
          LocalDate.now,
          patRef,
          Some(LocalDate.now.minusDays(3)),
          None
        )

      therapy <-
        genTherapy(
          patient,
          carePlan.therapyRecommendations.get.head
        )

    } yield
      RDPatientRecord(
        patient,
        NonEmptyList.one(episode),
        NonEmptyList.one(diag),
        Some(List(gmfcsStatus)),
        Some(hospitalization),
        NonEmptyList.fromListUnsafe(hpoTerms),
        Some(List(ngsReport)),
        Some(List(initBoardPlan,carePlan)),
        Some(List(followUp)),
        Some(List(therapy))
      )

}

object Generators extends Generators
