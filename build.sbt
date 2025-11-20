// build.sbt adapted from https://github.com/pbassiner/sbt-multi-project-example/blob/master/build.sbt

import scala.util.Properties.envOrElse


name := "rd-model"
ThisBuild / organization := "de.dnpm.dip"
ThisBuild / scalaVersion := "2.13.16"
ThisBuild / version      := envOrElse("VERSION","1.1.2")

val ownerRepo  = envOrElse("REPOSITORY","dnpm-dip/rd-model").split("/")
ThisBuild / githubOwner      := ownerRepo(0)
ThisBuild / githubRepository := ownerRepo(1)


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


lazy val generators = project
  .settings(
    name := "rd-dto-generators",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest,
      dependencies.generators,
      dependencies.icd10gm,
      dependencies.icd_catalogs,
      dependencies.atc_impl,
      dependencies.atc_catalogs,
      dependencies.json_schema_validator
    )
  )
  .dependsOn(
    dto_model,
    alpha_id_se % Test,
    orphanet % Test
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
    alpha_id_se % Test,
    orphanet % Test
  )




//-----------------------------------------------------------------------------
// DEPENDENCIES
//-----------------------------------------------------------------------------

lazy val dependencies =
  new {
    val scalatest             = "org.scalatest"          %% "scalatest"              % "3.2.18" % Test
    val scala_xml             = "org.scala-lang.modules" %% "scala-xml"              % "2.0.1"
    val core                  = "de.dnpm.dip"            %% "core"                   % "1.1.4"
    val generators            = "de.ekut.tbi"            %% "generators"             % "1.0.0"
    val icd10gm               = "de.dnpm.dip"            %% "icd10gm-impl"           % "1.1.2" % Test
    val icd_catalogs          = "de.dnpm.dip"            %% "icd-claml-packaged"     % "1.1.2" % Test
    val atc_impl              = "de.dnpm.dip"            %% "atc-impl"               % "1.1.0" % Test
    val atc_catalogs          = "de.dnpm.dip"            %% "atc-catalogs-packaged"  % "1.1.0" % Test
    val json_schema_validator = "com.networknt"          %  "json-schema-validator"  % "1.5.6" % Test
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
)


lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.githubPackages("dnpm-dip"),
    Resolver.githubPackages("KohlbacherLab"),
    Resolver.sonatypeCentralSnapshots
  )
)

