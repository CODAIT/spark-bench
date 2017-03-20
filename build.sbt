import Dependencies._

//// yes, name and organization have to be outside of commonSettings otherwise SBT explodes
//name := "sparkbench"
//organization := "com.ibm.sparktc"
//scalaVersion := "2.11.8"
//sbtVersion := "0.13"

lazy val commonSettings = Seq(
  organization := "com.ibm.sparktc",
  scalaVersion := "2.11.8"
//  ,
//  name := "sparkbench"
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    scalacOptions ++= Seq("-feature")
  )
  .aggregate(utils, workloads, datageneration, cli
  )


lazy val utils = project.settings(commonSettings)
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


//lazy val root = (project in file("."))
//  .settings(commonSettings)
//  .aggregate(utils, workloads, datageneration, cli)

//lazy val utils = project.settings(commonSettings: _*)
//lazy val workloads = project.settings(commonSettings: _*)
//lazy val datageneration = project.settings(commonSettings: _*)
//lazy val cli = project.settings(commonSettings: _*).dependsOn(workloads, datageneration)
