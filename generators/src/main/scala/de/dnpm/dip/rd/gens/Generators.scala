package de.dnpm.dip.rd.gens


import java.net.URI
import java.time.LocalDate
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
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.coding.hgvs.HGVS
import de.dnpm.dip.model.{
  Id,
  Age,
  Episode,
  ExternalId,
  Reference,
  Gender,
  Patient,
  Period,
  Publication,
  PubMed,
  TransferTAN
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
//    genT: Gen[Coding[T]],
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


  implicit def genDiagnosisCatagoryCoding: Gen[Coding[RDDiagnosis.Category]] =
    Gen.of[Coding[Orphanet]]
      .map(_.asInstanceOf[Coding[RDDiagnosis.Category]])


  implicit val genPubMedId: Gen[Reference[Publication]] =
    Gen.ints
      .map(_.toString)
      .map(ExternalId[Publication,PubMed](_))
      .map(Reference.from(_))


  implicit val genPatient: Gen[Patient] =
    for {
      id <-
        Gen.of[Id[Patient]]
      gender <-
        Gen.of[Coding[Gender.Value]]
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
        None
      )


  implicit val genDiagnosis: Gen[RDDiagnosis] =
    for {
      id       <- Gen.of[Id[RDDiagnosis]]
      patient  <- Gen.of[Id[Patient]]
      date     <- Gen.const(LocalDate.now).map(Some(_))
      category <- Gen.of[Coding[RDDiagnosis.Category]]
      onsetAge <- Gen.intsBetween(12,42).map(Age(_))
      status   <- Gen.of[Coding[RDDiagnosis.Status.Value]]
    } yield
      RDDiagnosis(
        id,
        Reference.from(patient),
        date,
        NonEmptyList.one(category),
        Some(onsetAge),
        false,
        status
      )


  def genCase(
    patient: Patient,
    diagnoses: List[RDDiagnosis]
  ): Gen[RDCase] =
    for {
      id    <- Gen.of[Id[RDCase]]
      ttan  <- Gen.of[Id[TransferTAN]]
      extId <- Gen.of[ExternalId[RDCase]]
      gmId <- genExternalId[RDCase,GestaltMatcher]
    } yield
      RDCase(
        id,
        Some(extId),
        Reference.to(patient),
        Some(ttan),
        Some(gmId),
        diagnoses.map(Reference.to(_)),
      )

  implicit val genHPOTerm: Gen[HPOTerm] =
    for {
      id    <- Gen.of[Id[HPOTerm]]
      pat   <- Gen.of[Id[Patient]]
      value <- Gen.of[Coding[HPO]]
    } yield
      HPOTerm(
        id,
        Reference.from(pat),
        value
      )


  implicit val genAutozygosity: Gen[Autozygosity] =
    for {
      id    <- Gen.of[Id[Autozygosity]]
      pat   <- Gen.of[Id[Patient]]
      value <- Gen.floats
    } yield
      Autozygosity(
        id,
        Reference.from(pat),
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
    .map(Coding[HGVS](_))

  // Source: https://varnomen.hgvs.org/recommendations/DNA/variant/duplication/
  private val gDNAChanges =
    Seq(
      "NC_000023.10:g.33038255C>A",
      "NC_000023.11:g.32343183dup",
      "NC_000023.11:g.(31060227_31100351)_(33274278_33417151)dup",
      "NC_000023.11:g.pter_qtersup",
      "NC_000023.11:g.33344590_33344592=/dup",
    )
    .map(Coding[HGVS](_))

  private val proteinChanges =
    Seq(
      "LRG_199p1:p.Trp24Cys",
      "LRG_199p1:p.Trp24Ter (p.Trp24*)",
      "NP_003997.1:p.(Gly56Ala^Ser^Cys)",
      "LRG_199p1:p.Trp24=/Cys",
      "NP_003997.2:p.Val7dup",
    )
    .map(Coding[HGVS](_))


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


  implicit val genSmallVariant: Gen[SmallVariant] =
    for {
      id    <- Gen.of[Id[SmallVariant]]
      pat   <- Gen.of[Id[Patient]]
      chr   <- Gen.of[Coding[Chromosome.Value]]
      gene  <- Gen.of[Coding[HGNC]]
      pos   <- Gen.ints
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
        Reference.from(pat),
        chr,
        Some(Set(gene)),
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

  implicit val genStructuralVariant: Gen[StructuralVariant] =
    for {
      id    <- Gen.of[Id[SmallVariant]]
      pat   <- Gen.of[Id[Patient]]
      gene  <- Gen.of[Coding[HGNC]]
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
        Reference.from(pat),
        Some(Set(gene)),
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

  implicit val genCopyNumberVariant: Gen[CopyNumberVariant] =
    for {
      id    <- Gen.of[Id[SmallVariant]]
      pat   <- Gen.of[Id[Patient]]
      chr   <- Gen.of[Coding[Chromosome.Value]]
      gene  <- Gen.of[Coding[HGNC]]
      start <- Gen.ints
      end   <- Gen.ints
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
        Reference.from(pat),
        chr,
        Some(Set(gene)),
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



  implicit val genNGSReport: Gen[RDNGSReport] =
    for {
      id  <- Gen.of[Id[RDNGSReport]]
      pat <- Gen.of[Id[Patient]]
      lab <- Gen.of[Id[Lab]].map(Reference.from(_).copy(display = Some("Lab name")))
      typ <- Gen.of[Coding[RDNGSReport.Type.Value]]
      familyControl <- Gen.of[Coding[RDNGSReport.FamilyControlLevel.Value]]

      metaInfo <-
        for {
          platform <- Gen.of[Coding[RDNGSReport.Platform]]
        } yield RDNGSReport.SequencingInfo(
          platform,
          "Kit..."
        )
      autoZyg <- Gen.of[Autozygosity]
      smallVariants <- Gen.list(Gen.intsBetween(3,5),Gen.of[SmallVariant])
      cnvs <- Gen.list(Gen.intsBetween(3,5),Gen.of[CopyNumberVariant])
      svs <- Gen.list(Gen.intsBetween(3,5),Gen.of[StructuralVariant])
    } yield
      RDNGSReport(
        id,
        Reference.from(pat),
        lab,
        Some(LocalDate.now),
        typ,
        familyControl,
        metaInfo,
        Some(autoZyg),
        Some(smallVariants),
        Some(cnvs),
        Some(svs),
      )


  implicit val genRDTherapy: Gen[RDTherapy] =
    for { 
      id <- Gen.of[Id[RDTherapy]]
      pat <- Gen.of[Id[Patient]]
      notes <- Gen.const("Notes on the therapy...")
    } yield
      RDTherapy(
        id,
        Reference.from(pat),
        notes
      )


  implicit val genRDPatientRecord: Gen[RDPatientRecord] =
    for {
      patient <-
        Gen.of[Patient]

      patRef = Reference.to(patient)

      diag <-
        Gen.of[RDDiagnosis]
          .map(_.copy(patient = patRef))

      cse <-
        genCase(patient,List(diag))

      hpoTerms <-
        Gen.list(
          Gen.intsBetween(3,5),
          Gen.of[HPOTerm].map(_.copy(patient = patRef))
        )
        .map(_.distinctBy(_.value.code))

      ngsReport <-
        Gen.of[RDNGSReport]
          .map(
            ngs =>
              ngs.copy(
                patient = patRef,
                autozygosity = ngs.autozygosity.map(_.copy(patient = patRef)),
                smallVariants = ngs.smallVariants.map(_.map(_.copy(patient = patRef))),
                copyNumberVariants = ngs.copyNumberVariants.map(_.map(_.copy(patient = patRef))),
                structuralVariants = ngs.structuralVariants.map(_.map(_.copy(patient = patRef)))
              )
          )

      therapy <-
        Gen.of[RDTherapy]
          .map(_.copy(patient = patRef))

    } yield
      RDPatientRecord(
        patient,
        JsObject.empty,
        NonEmptyList.one(cse),
        diag,
        NonEmptyList.fromListUnsafe(hpoTerms),
        NonEmptyList.one(ngsReport),
        Some(therapy)
      )

}

object Generators extends Generators
