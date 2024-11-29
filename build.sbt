
/*
 build.sbt adapted from https://github.com/pbassiner/sbt-multi-project-example/blob/master/build.sbt
*/


name := "rd-model"
ThisBuild / organization := "de.dnpm.dip"
ThisBuild / scalaVersion := "2.13.13"
ThisBuild / version      := "1.0-SNAPSHOT"


//-----------------------------------------------------------------------------
// PROJECTS
//-----------------------------------------------------------------------------

lazy val global = project
  .in(file("."))
  .settings(
    settings,
    publish / skip := true
  )
  .aggregate(
     dto_model,
     hpo,
     alpha_id_se,
     omim,
     orphanet,
     generators,
     tests
  )


lazy val dto_model = project
  .settings(
    name := "rd-dto-model",
    settings,
    libraryDependencies ++= Seq(
      dependencies.core,
      dependencies.scalatest
    )
  )

lazy val hpo = project
  .settings(
    name := "hp-ontology",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest
    )
  )
  .dependsOn(dto_model)

lazy val orphanet = project
  .settings(
    name := "orphanet-ordo",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scala_xml,
      dependencies.scalatest
    )
  )
  .dependsOn(dto_model)

lazy val alpha_id_se = project
  .settings(
    name := "alpha-id-se",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest
    )
  )
  .dependsOn(dto_model)

lazy val omim = project
  .settings(
    name := "omim-catalog",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest
    )
  )
  .dependsOn(dto_model)

lazy val generators = project
  .settings(
    name := "rd-dto-generators",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest,
      dependencies.generators,
      dependencies.atc_impl,
      dependencies.atc_catalogs
    )
  )
  .dependsOn(
    dto_model
  )

lazy val tests = project
  .settings(
    name := "tests",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest,
      dependencies.icd10gm,
      dependencies.icd_catalogs
    ),
    publish / skip := true
  )
  .dependsOn(
    dto_model,
    orphanet % Test,
    omim % Test,
  )




//-----------------------------------------------------------------------------
// DEPENDENCIES
//-----------------------------------------------------------------------------

lazy val dependencies =
  new {
    val scalatest    = "org.scalatest"          %% "scalatest"              % "3.2.17" % Test
    val scala_xml    = "org.scala-lang.modules" %% "scala-xml"              % "2.0.1"
    val core         = "de.dnpm.dip"            %% "core"                   % "1.0-SNAPSHOT"
    val generators   = "de.ekut.tbi"            %% "generators"             % "1.0-SNAPSHOT"
    val icd10gm      = "de.dnpm.dip"            %% "icd10gm-impl"           % "1.0-SNAPSHOT" % Test
    val icd_catalogs = "de.dnpm.dip"            %% "icd-claml-packaged"     % "1.0-SNAPSHOT" % Test
    val atc_impl     = "de.dnpm.dip"            %% "atc-impl"               % "1.0-SNAPSHOT" % Test
    val atc_catalogs = "de.dnpm.dip"            %% "atc-catalogs-packaged"  % "1.0-SNAPSHOT" % Test
  }


//-----------------------------------------------------------------------------
// SETTINGS
//-----------------------------------------------------------------------------

lazy val settings = commonSettings


// Compiler options from: https://alexn.org/blog/2020/05/26/scala-fatal-warnings/
lazy val compilerOptions = Seq(
  // Feature options
  "-encoding", "utf-8",
  "-explaintypes",
  "-feature",
  "-language:existentials",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ymacro-annotations",

  // Warnings as errors!
  "-Xfatal-warnings",

  // Linting options
  "-unchecked",
  "-Xcheckinit",
  "-Xlint:adapted-args",
  "-Xlint:constant",
  "-Xlint:delayedinit-select",
  "-Xlint:deprecation",
  "-Xlint:doc-detached",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",
  "-Wdead-code",
  "-Wextra-implicit",
  "-Wnumeric-widen",
  "-Wunused:imports",
  "-Wunused:locals",
  "-Wunused:patvars",
  "-Wunused:privates",
  "-Wunused:implicits",
  "-Wvalue-discard",

  // Deactivated to avoid many false positives from 'evidence' parameters in context bounds
//  "-Wunused:params",
)


lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq("Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository") ++
    Resolver.sonatypeOssRepos("releases") ++
    Resolver.sonatypeOssRepos("snapshots")
)

