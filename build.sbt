import Dependencies._

lazy val commonSettings = Seq(
  organization := "com.ibm.sparktc",
  scalaVersion := "2.11.8",
  parallelExecution in Test := false
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    scalacOptions ++= Seq("-feature", "-Ylog-classpath")
  )
  .aggregate(utils, workloads, datageneration, cli
  )


lazy val utils = project
  .settings(
    commonSettings,
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= testDeps
  )

lazy val workloads = project
  .settings(
    commonSettings,
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= otherCompileDeps,
    libraryDependencies ++= testDeps
  )
  .dependsOn(utils)

lazy val datageneration = project
  .settings(
    commonSettings,
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= otherCompileDeps,
    libraryDependencies ++= testDeps
  )
  .dependsOn(utils)

lazy val cli = project
  .settings(
    commonSettings,
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= testDeps
  )
  .dependsOn(workloads, datageneration, utils)
