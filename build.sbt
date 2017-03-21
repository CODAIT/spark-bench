import Dependencies._

lazy val commonSettings = Seq(
  organization := "com.ibm.sparktc",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    scalacOptions ++= Seq("-feature")
  )
  .aggregate(utils, workloads, datageneration, cli
  )


lazy val utils = project
  .settings(commonSettings)

lazy val workloads = project
  .settings(
    commonSettings,
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= otherCompileDeps,
    libraryDependencies ++= testDeps
  )

lazy val datageneration = project
  .settings(
    commonSettings,
    libraryDependencies ++= sparkDeps,
    libraryDependencies ++= otherCompileDeps,
    libraryDependencies ++= testDeps
  )

lazy val cli = project
  .settings(
    commonSettings,
    libraryDependencies ++= testDeps
  )
  .dependsOn(workloads, datageneration)
