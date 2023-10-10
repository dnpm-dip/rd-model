package de.dnpm.dip.rd.gens


import java.net.URI
import java.time.LocalDate
import de.ekut.tbi.generators.{
  Gen,
}
import de.ekut.tbi.generators.DateTimeGens._
import de.dnpm.dip.coding.{
  Coding,
  CodeSystem,
}
import de.dnpm.dip.coding.hgnc.HGNC
import de.dnpm.dip.model.{
  Id,
  ExternalId,
  Reference,
  Gender,
  Patient
}
import de.dnpm.dip.rd.model._



trait Generators
{

  implicit def genId[T]: Gen[Id[T]] =
    Gen.uuidStrings
      .map(Id(_))


  implicit def genExternalId[T]: Gen[ExternalId[T]] =
    Gen.uuidStrings
      .map(ExternalId(_,None))


  implicit def genCodingfromCodeSystem[S: Coding.System: CodeSystem]: Gen[Coding[S]] =
    Gen.oneOf(CodeSystem[S].concepts)
      .map(Coding.fromConcept(_))


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


  implicit val genClinician: Gen[Clinician] =
    Gen.const("Dr. House")
      .map(Clinician(_))


  implicit val genDiagnosis: Gen[RDDiagnosis] =
    for {
      id       <- Gen.of[Id[RDDiagnosis]]
      patient  <- Gen.of[Id[Patient]]
      date     <- Gen.const(LocalDate.now).map(Some(_))
      category <- Gen.of[Coding[RDDiagnosis.Category]]
      status   <- Gen.of[Coding[RDDiagnosis.Status.Value]]
    } yield
      RDDiagnosis(
        id,
        Reference(patient,None),
        date,
        category,
        status
      )


  implicit val genCase: Gen[RDCase] =
    for {
      id    <- Gen.of[Id[RDCase]]
      extId <- Gen.of[ExternalId[RDCase]]
      patient <- Gen.of[Id[Patient]]
      gmId  <- Gen.of[ExternalId[RDCase]]
                 .map(_.copy(system = Some(URI.create("https://www.gestaltmatcher.org/"))))
                 .map(Some(_))
      f2gId <- Gen.of[ExternalId[RDCase]]
                 .map(_.copy(system = Some(URI.create("https://www.face2gene.com/"))))
                 .map(Some(_))
      date  <- Gen.const(LocalDate.now).map(Some(_))
      referrer <- Gen.of[Clinician]
      diagnosis <- Gen.of[Id[RDDiagnosis]]
    } yield
      RDCase(
        id,
        extId,
        gmId,
        f2gId,
        Reference(patient,None),
        date,
        referrer,
        Reference(diagnosis,None)
      )


/*
  implicit val genHpoCoding: Gen[Coding[HPO]] =
    Gen.intsBetween(0,200000)
      .map(c => Coding[HPO](s"DummyHP:$c"))
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

  implicit val genHPOTerm: Gen[HPOTerm] =
    for {
      id    <- Gen.of[Id[HPOTerm]]
      pat   <- Gen.of[Id[Patient]]
      value <- Gen.of[Coding[HPO]]
    } yield
      HPOTerm(
        id,
        Reference(pat,None),
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
        Reference(pat,None),
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


  implicit val genVariant: Gen[Variant] =
    for {
      id  <- Gen.of[Id[Variant]]
      pat <- Gen.of[Id[Patient]]
      gene <- Gen.of[Coding[HGNC]]
      loe <- Gen.const("Level of evidence")
      iscn <- Gen.const("Some ISCN description")
      pmid <- Gen.ints.map(_.toString)
      cDNAChg <- Gen.oneOf(cDNAChanges)
      gDNAChg <- Gen.oneOf(gDNAChanges)
      proteinChg <- Gen.oneOf(proteinChanges)
      acmgClass <- Gen.of[Coding[Variant.ACMGClass]]
      zygosity <- Gen.of[Coding[Variant.Zygosity]]
      deNovo <- Gen.of[Coding[Variant.DeNovo]]
      inh <- Gen.of[Coding[Variant.InheritanceMode]]
      signif <- Gen.of[Coding[Variant.Significance]]
      clinVar <- Gen.uuidStrings.map(Set(_))
    } yield 
      Variant(
        id,
        Reference(pat,None),
        gene,
        Some(loe),
        Some(iscn),
        Some(ExternalId(pmid,"https://pubmed.ncbi.nlm.nih.gov/")),
        Some(cDNAChg),
        Some(gDNAChg),
        Some(proteinChg),
        Some(acmgClass),
        Some(zygosity),
        Some(deNovo),
        Some(inh),
        Some(signif),
        Some(clinVar)
      )


  implicit val genNGSReport: Gen[RDNGSReport] =
    for {
      id  <- Gen.of[Id[RDNGSReport]]
      pat <- Gen.of[Id[Patient]]
      lab =  Lab("Lab name")
      typ <- Gen.of[Coding[RDNGSReport.Type]]
      metaInfo = RDNGSReport.MetaInfo("Seq. Type", "Kit...")
      autoZyg <- Gen.of[Autozygosity]
      variants <- Gen.list(Gen.intsBetween(3,6),Gen.of[Variant])
    } yield
      RDNGSReport(
        id,
        Reference(pat,None),
        lab,
        Some(LocalDate.now),
        typ,
        metaInfo,
        Some(autoZyg),
        Some(variants)
      )


  implicit val genRDTherapy: Gen[RDTherapy] =
    for { 
      id <- Gen.of[Id[RDTherapy]]
      pat <- Gen.of[Id[Patient]]
      notes <- Gen.const("Notes on the therapy...")
    } yield
      RDTherapy(
        id,
        Reference(pat,None),
        notes
      )


  implicit val genRDPatientRecord: Gen[RDPatientRecord] =
    for {
      patient <-
        Gen.of[Patient]

      patRef = Reference(patient)

      diag <-
        Gen.of[RDDiagnosis]
          .map(_.copy(patient = patRef))

      cse <-
        Gen.of[RDCase]
          .map(
            _.copy(
              patient = patRef,
              reason = Reference(diag)
            )
          ) 

      hpoTerms <-
        Gen.list(
          Gen.intsBetween(3,5),
          Gen.of[HPOTerm].map(_.copy(patient = patRef))
        )

      ngsReport <-
        Gen.of[RDNGSReport]
          .map(
            ngs =>
              ngs.copy(
                patient = patRef,
                autozygosity = ngs.autozygosity.map(_.copy(patient = patRef)),
                variants = ngs.variants.map(_.map(_.copy(patient = patRef)))
              )
          )

      therapy <-
        Gen.of[RDTherapy]
          .map(_.copy(patient = patRef))

    } yield
      RDPatientRecord(
        patient,
        diag,
        cse,
        Some(hpoTerms),
        ngsReport,
        Some(therapy)
      )

}

object Generators extends Generators
