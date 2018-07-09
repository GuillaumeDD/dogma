import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object HelloBuild extends Build {
  lazy val global = Project(id = "Global", base = file(".")) aggregate(predicateCalculus, dogma,scadia)

  lazy val dogma = Project(id = "Dogma",
                            base = file("Dogma"),
			 settings = dogmaBuildSettings ++ assemblySettings) dependsOn(predicateCalculus, simpleSemantics) settings (
    // Assembly settings
    mainClass in assembly := Some("fr.dubuissonduplessis.dogma.examples.gdd.app.MainScenario")
  )

  lazy val predicateCalculus = Project(id = "PredicateCalculus",
                            base = file("PredicateCalculus"))

  lazy val simpleSemantics = Project(id = "SimpleSemantics",
                            base = file("SimpleSemantics")) dependsOn(predicateCalculus)

  lazy val dogmaBuildSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1-SNAPSHOT",
    organization := "fr.dubuissonduplessis.dogma",
    scalaVersion := "2.11.1"
  )

  lazy val scadiaBuildSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1-SNAPSHOT",
    organization := "fr.lifl.smac.scadia",
    scalaVersion := "2.10.3"
  )

  lazy val scadia = Project(id = "ScaDia", base = file("ScaDia"),
    settings = scadiaBuildSettings ++ assemblySettings) dependsOn(dogma, predicateCalculus) settings(
      // your settings here
    )
}

