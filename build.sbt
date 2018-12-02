name := """ToDoList"""
organization := "com.todos"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  "org.playframework.anorm" %% "anorm" % "2.6.1",
  "org.mockito" % "mockito-core" % "2.7.22" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc4"
)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.robinnagpal.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.robinnagpal.binders._"

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"