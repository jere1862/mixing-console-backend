name := """mixing-console-backend"""
organization := "org.mutecc"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.12.0" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.0.0" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test
libraryDependencies += ws
libraryDependencies += filters

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "org.mutecc.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "org.mutecc.binders._"
