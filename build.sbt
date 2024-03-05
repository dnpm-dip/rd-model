
/*
 build.sbt adapted from https://github.com/pbassiner/sbt-multi-project-example/blob/master/build.sbt
*/


name := "rd-model"
ThisBuild / organization := "de.dnpm.dip"
ThisBuild / scalaVersion := "2.13.12"
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
     json_schema,
     hpo,
     omim,
     orphanet,
     generators
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

lazy val json_schema = project
  .settings(
    name := "rd-json-schema",
    settings,
    libraryDependencies ++= Seq(
      dependencies.scalatest,
//      dependencies.scala_jsonschema,
//      dependencies.scala_jsonschema_cats,
//      dependencies.scala_jsonschema_play,

    )
  )
  .dependsOn(dto_model)

lazy val hpo = project
  .settings(
    name := "hp-ontology",
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


lazy val generators = project
  .settings(
    name := "rd-dto-generators",
    settings,
    libraryDependencies ++= Seq(
      dependencies.generators,
      dependencies.scalatest
    )
  )
  .dependsOn(
    dto_model
  )



//-----------------------------------------------------------------------------
// DEPENDENCIES
//-----------------------------------------------------------------------------

lazy val dependencies =
  new {
    val scalatest             = "org.scalatest"          %% "scalatest"                  % "3.2.17" % Test
    val scala_xml             = "org.scala-lang.modules" %% "scala-xml"                  % "2.0.1"
    val core                  = "de.dnpm.dip"            %% "core"                       % "1.0-SNAPSHOT"
    val generators            = "de.ekut.tbi"            %% "generators"                 % "1.0-SNAPSHOT"
//    val scala_jsonschema      = "com.github.andyglow"    %% "scala-jsonschema"           % "0.7.11"
//    val scala_jsonschema_cats = "com.github.andyglow"    %% "scala-jsonschema-cats"      % "0.7.11"
//    val scala_jsonschema_play = "com.github.andyglow"    %% "scala-jsonschema-play-json" % "0.7.11"

  }


//-----------------------------------------------------------------------------
// SETTINGS
//-----------------------------------------------------------------------------

lazy val settings = commonSettings


lazy val compilerOptions = Seq(
  "-encoding", "utf8",
  "-unchecked",
  "-feature",
  "-language:postfixOps",
  "-Xfatal-warnings",
  "-deprecation",
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq("Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository") ++
    Resolver.sonatypeOssRepos("releases") ++
    Resolver.sonatypeOssRepos("snapshots")
)

