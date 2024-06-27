package de.dnpm.dip.rd.gens


import java.net.URI
import java.time.{
  LocalDate,
  YearMonth
}
import cats.data.NonEmptyList
import play.api.libs.json.JsObject
import de.ekut.tbi.generators.{
  Gen,
}
import de.ekut.tbi.generators.DateTimeGens._
import de.dnpm.dip.coding.{
  Coding,
  CodeSystem,
}
import de.dnpm.dip.coding.atc.ATC
import de.dnpm.dip.coding.atc.Kinds.Substance
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.coding.hgvs.HGVS
import de.dnpm.dip.model.{
  Id,
  Age,
  Duration,
  ExternalId,
  Reference,
  Gender,
  History,
  NGSReport,
  Patient,
  Period,
  Publication,
  PubMed,
  Study,
  TransferTAN,
  UnitOfTime
}
import de.dnpm.dip.rd.model._


trait Generators
{

  implicit def genId[T]: Gen[Id[T]] =
    Gen.uuidStrings
      .map(Id(_))


  implicit def genExtId[T]: Gen[ExternalId[T]] =
    Gen.uuidStrings
      .map(ExternalId(_,None))


  def genExternalId[T,S: Coding.System]: Gen[ExternalId[T]] =
    Gen.uuidStrings
       .map(ExternalId[T,S](_))


  implicit def genReference[T]: Gen[Reference[T]] =
    Gen.of[Id[T]]
      .map(Reference.from(_))


  implicit def genCodingfromCodeSystem[S: Coding.System: CodeSystem]: Gen[Coding[S]] =
    Gen.oneOf(CodeSystem[S].concepts)
      .map(Coding.fromConcept(_))

/*
  import shapeless.{Coproduct, :+:, CNil, Inl, Inr, Nat}
  import shapeless.ops.coproduct
  import shapeless.ops.nat.ToInt

  implicit def genCoproductCoding[H: Coding.System, T <: Coproduct, L <: Nat](
    implicit
    genH: Gen[Coding[H]],
    len: coproduct.Length.Aux[T, L],
    lenAsInt: ToInt[L]
  ): Gen[Coding[H :+: T]] =
    for {
      p <- Gen.doubles

      coding <-
        if (p < 1.0/(1 + lenAsInt())) genH
        else genT

      coding <- genH  
    } yield coding.asInstanceOf[Coding[H :+: T]]


  implicit def genCoproductTermination[H: Coding.System](
    implicit genH: Gen[Coding[H]]
  ): Gen[Coding[H :+: CNil]] =
    genH.map(_.asInstanceOf[Coding[H :+: CNil]])
*/

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



  implicit val genOrphanetCoding: Gen[Coding[Orphanet]] =
    Gen.oneOf(
      "ORPHA:98907",
      "ORPHA:98897",
      "ORPHA:98408",
      "ORPHA:98375",
      "ORPHA:984"
    )
    .map(Coding[Orphanet](_))


  private val atcRegex =
    """B06AC|C09X|C10A""".r.unanchored

  private implicit lazy val atc: CodeSystem[ATC] =
    ATC.Catalogs
      .getInstance[cats.Id]
      .get
      .latest
      .filter(c => atcRegex matches c.code.value)
      .filter(ATC.filterByKind(Substance))



  implicit def genDiagnosisCatagoryCoding: Gen[Coding[RDDiagnosis.Category]] =
    Gen.of[Coding[Orphanet]]
      .map(_.asInstanceOf[Coding[RDDiagnosis.Category]])


  implicit val genPubMedId: Gen[Reference[Publication]] =
    Gen.positiveInts
      .map(_.toString)
      .map(ExternalId[Publication,PubMed](_))
      .map(Reference.from(_))

  private val genGender: Gen[Coding[Gender.Value]] =
    Gen.distribution(
      48.0 -> Gender.Male,
      48.0 -> Gender.Female,
      4.0  -> Gender.Other,
    )
    .map(Coding(_))

  implicit val genPatient: Gen[Patient] =
    for {
      id <-
        Gen.of[Id[Patient]]

      gender <-
        genGender
        
      birthdate <-
        localDatesBetween(
          LocalDate.now.minusYears(70),
          LocalDate.now.minusYears(25)
        )
    } yield
      Patient(
        id,
        gender,
        birthdate,
        None,
        None,
        None,
        None
      )


  def genDiagnosis(
    patient: Patient,
  ): Gen[RDDiagnosis] =
    for {
      id       <- Gen.of[Id[RDDiagnosis]]
      date     <- Gen.const(LocalDate.now).map(Some(_))
      category <- Gen.of[Coding[RDDiagnosis.Category]]
      onsetAge <- Gen.intsBetween(12,42).map(Age(_))
      status   <- Gen.of[Coding[RDDiagnosis.VerificationStatus.Value]]
    } yield
      RDDiagnosis(
        id,
        Reference.to(patient),
        date,
        NonEmptyList.one(category),
        Some(onsetAge),
        status
      )


  def genEpisode(
    patient: Patient,
  ): Gen[RDEpisodeOfCare] =
    for {
      id    <- Gen.of[Id[RDEpisodeOfCare]]
      ttan  <- Gen.of[Id[TransferTAN]]
    } yield
      RDEpisodeOfCare(
        id,
        Reference.to(patient),
        Some(ttan),
        Period(LocalDate.now)
      )

  def genHPOTerm(
    patient: Patient
  ): Gen[HPOTerm] =
    for {
      id     <- Gen.of[Id[HPOTerm]]
      value  <- Gen.of[Coding[HPO]]
      status <- Gen.of[Coding[HPOTerm.Status.Value]]
    } yield
      HPOTerm(
        id,
        Reference.to(patient),
        YearMonth.now.minusMonths(5),
        value,
        Some(
          List(
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

  // Source: https://varnomen.hgvs.org/recommendations/DNA/variant/substitution/
  private val cDNAChanges =
    Seq(
      "NG_012232.1(NM_004006.2):c.93+1G>T",
      "LRG_199t1:c.79_80delinsTT",
      "NM_004006.2:c.145_147delinsTGG",
      "G_199t1:c.54G>H",
      "LRG_199t1:c.85=/T>C",
      "NM_004006.2:c.123=",
    )
    .map(Coding[HGVS.DNA](_))

  // Source: https://varnomen.hgvs.org/recommendations/DNA/variant/duplication/
  private val gDNAChanges =
    Seq(
      "NC_000023.10:g.33038255C>A",
      "NC_000023.11:g.32343183dup",
      "NC_000023.11:g.(31060227_31100351)_(33274278_33417151)dup",
      "NC_000023.11:g.pter_qtersup",
      "NC_000023.11:g.33344590_33344592=/dup",
    )
    .map(Coding[HGVS.DNA](_))

  private val proteinChanges =
    Seq(
      "LRG_199p1:p.Trp24Cys",
      "LRG_199p1:p.Trp24Ter (p.Trp24*)",
      "NP_003997.1:p.(Gly56Ala^Ser^Cys)",
      "LRG_199p1:p.Trp24=/Cys",
      "NP_003997.2:p.Val7dup",
    )
    .map(Coding[HGVS.Protein](_))


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
      chr   <- Gen.of[Coding[Chromosome.Value]]
      gene  <- Gen.of[Coding[HGNC]]
      localization <- Gen.of[Coding[Variant.Localization.Value]]
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
      inh <- Gen.of[Coding[Variant.InheritanceMode.Value]]
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
        Some(clinVarId),
        Some(Set(pmid))
      )

  def genStructuralVariant(
    patient: Patient
  ): Gen[StructuralVariant] =
    for {
      id    <- Gen.of[Id[SmallVariant]]
      gene  <- Gen.of[Coding[HGNC]]
      localization <- Gen.of[Coding[Variant.Localization.Value]]
      iscn  <- Gen.const(Coding[ISCN]("Some ISCN description"))
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
      inh <- Gen.of[Coding[Variant.InheritanceMode.Value]]
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
        Some(clinVarId),
        Some(Set(pmid))
      )

  def genCopyNumberVariant(
    patient: Patient
  ): Gen[CopyNumberVariant] =
    for {
      id    <- Gen.of[Id[SmallVariant]]
      chr   <- Gen.of[Coding[Chromosome.Value]]
      gene  <- Gen.of[Coding[HGNC]]
      localization <- Gen.of[Coding[Variant.Localization.Value]]
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
      inh <- Gen.of[Coding[Variant.InheritanceMode.Value]]
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
        Some(clinVarId),
        Some(Set(pmid))
      )



  def genNGSReport(
    patient: Patient
  ): Gen[RDNGSReport] =
    for {
      id  <- Gen.of[Id[RDNGSReport]]
      lab <- Gen.of[Id[Lab]].map(Reference.from(_).copy(display = Some("Lab name")))
      typ <- Gen.of[Coding[NGSReport.SequencingType.Value]]
      familyControl <- Gen.of[Coding[RDNGSReport.FamilyControlLevel.Value]]

      metaInfo <-
        for {
          platform <- Gen.of[Coding[NGSReport.Platform.Value]]
        } yield RDNGSReport.SequencingInfo(
          platform,
          "Kit..."
        )
      autoZyg <- genAutozygosity(patient)
      smallVariants <- Gen.list(Gen.intsBetween(3,5),genSmallVariant(patient))
      cnvs <- Gen.list(Gen.intsBetween(3,5),genCopyNumberVariant(patient))
      svs <- Gen.list(Gen.intsBetween(3,5),genStructuralVariant(patient))
    } yield
      RDNGSReport(
        id,
        Reference.to(patient),
        lab,
        LocalDate.now,
        typ,
        familyControl,
        metaInfo,
        Some(autoZyg),
        Some(smallVariants),
        Some(cnvs),
        Some(svs),
      )


  def genTherapyRecommendation(
    patient: Patient,
    variants: Seq[Reference[Variant]]
  ): Gen[RDTherapyRecommendation] =
    for {
      id <- Gen.of[Id[RDTherapyRecommendation]]
      category <- Gen.of[Coding[RDTherapy.Category.Value]]
      medication <- Gen.of[Coding[ATC]]
      supportingVariant <- Gen.oneOf(variants)
    } yield RDTherapyRecommendation(
      id,
      Reference.to(patient),
      LocalDate.now,
      category,
      Some(Set(medication)),
      Some(List(supportingVariant))
    )


  def genCarePlan(
    patient: Patient,
    variants: Seq[Reference[Variant]]
  ): Gen[RDCarePlan] =
    for {
      id <- Gen.of[Id[RDCarePlan]]

      protocol = "Protocol of the RD conference..."

      recommendation <-
        genTherapyRecommendation(
          patient,
          variants
        )

      studyEnrollmentRecommendation <-
        for {
          stId <- Gen.of[Id[RDStudyEnrollmentRecommendation]]
          studyNr <- Gen.intsBetween(10000000,50000000)
                     .map(s => ExternalId[Study](s"$s","???"))
        } yield RDStudyEnrollmentRecommendation(
          stId,
          Reference.to(patient),
          LocalDate.now,
          recommendation.supportingVariants,
          Some(List(studyNr))
        )

      clinicalManagementRecommendation <-
        for {
          recId <- Gen.of[Id[ClinicalManagementRecommendation]]
          typ <- Gen.of[Coding[ClinicalManagementRecommendation.Type.Value]]
        } yield ClinicalManagementRecommendation(
          recId,
          Reference.to(patient),
          LocalDate.now,
          typ
        )

    } yield RDCarePlan(
      id,
      Reference.to(patient),
      LocalDate.now,
      Some(true),
      Some(List(recommendation)),
      Some(studyEnrollmentRecommendation),
      Some(clinicalManagementRecommendation),
      Some(protocol)
    )


  def genTherapy(
    patient: Patient,
    recommendation: RDTherapyRecommendation
  ): Gen[History[RDTherapy]] =
    for { 
      id <- Gen.of[Id[RDTherapy]]
      notes <- Gen.const("Notes on the therapy...")
    } yield History(
      RDTherapy(
        id,
        Reference.to(patient),
        Some(Reference.to(recommendation)),
        LocalDate.now,
        Some(recommendation.category),
        recommendation.medication,
        Some(Period(recommendation.issuedOn)),
        Some(notes)
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

  def genHospitalization(
    patient: Patient
  ): Gen[Hospitalization] =
    for { 
      id <- Gen.of[Id[Hospitalization]]
      days <- Gen.intsBetween(5,42)
    } yield Hospitalization(
      id,
      Reference.to(patient),
      Some(
        Period(
          LocalDate.now.minusYears(5),
          LocalDate.now
        )
      ),
      Duration(days,UnitOfTime.Days)
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

      hospitalization <-
        genHospitalization(patient) 

      gmfcsStatus <-
        genGMFCS(patient)

      ngsReport <-
        genNGSReport(patient)

      carePlan <-
        genCarePlan(
          patient,
          ngsReport.variants.map(Reference.to(_))
        ) 

      therapy <-
        genTherapy(
          patient,
          carePlan.therapyRecommendations.get.head
        )

    } yield
      RDPatientRecord(
        patient,
        JsObject.empty,
        NonEmptyList.one(episode),
        diag,
        Some(List(gmfcsStatus)),
        Some(hospitalization),
        NonEmptyList.fromListUnsafe(hpoTerms),
        Some(List(ngsReport)),
        Some(List(carePlan)),
        Some(List(therapy))
      )

}

object Generators extends Generators
