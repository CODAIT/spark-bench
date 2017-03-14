name := "sparkbench"
organization := "com.ibm.sparktc"
scalaVersion := "2.11.8"
sbtVersion := "0.13"

lazy val workloads = project
lazy val datageneration = project
lazy val cli = project.dependsOn(workloads, datageneration)

//val scalacheckVersion = "1.12.5"
//val junitVersion = "4.12"
//val scalatestVersion = "3.0.1"
//
libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1"
//  "junit"                       % "junit"                  % junitVersion            % "test",
//  "org.scalacheck"              %% "scalacheck"            % scalacheckVersion       % "test",
//  "org.scalactic"               %% "scalactic"             % scalatestVersion        % "test",
//  "org.scalatest"               %% "scalatest"             % scalatestVersion        % "test"
)