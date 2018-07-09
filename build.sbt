name := "Dogma"

version := "0.0.1"

scalaVersion := "2.11.1"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:reflectiveCalls")

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.1"

libraryDependencies += "ch.qos.logback" % "logback-core" % "1.0.9"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.9"
