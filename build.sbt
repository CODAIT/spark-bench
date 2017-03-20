val sparkVersion = "2.1.0"
val scalacheckVersion = "1.11.4"
val junitVersion = "4.12"
val scalatestVersion = "2.2.4"

name := "sparkbench"
organization := "com.ibm.sparktc"

lazy val commonSettings = Seq(

    scalaVersion := "2.11.8",
    sbtVersion := "0.13",
    scalaVersion := "2.11.8",

    scalacOptions ++= Seq(
    "-feature"
  ),

  libraryDependencies ++= Seq(
    "org.apache.spark"            %% "spark-core"            % sparkVersion           % "provided",
    "org.apache.spark"            %% "spark-mllib"           % sparkVersion           % "provided",
    "org.jblas"                    % "jblas"                 % "1.2.4",
    "junit"                       % "junit"                  % junitVersion            % "test",
    "org.scalacheck"              %% "scalacheck"            % scalacheckVersion       % "test",
    "org.scalactic"               %% "scalactic"             % scalatestVersion        % "test",
    "org.scalatest"               %% "scalatest"             % scalatestVersion        % "test"
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(utils, workloads, datageneration, cli)

lazy val utils = project.settings(commonSettings: _*)
lazy val workloads = project.settings(commonSettings: _*)
lazy val datageneration = project.settings(commonSettings: _*)
lazy val cli = project.settings(commonSettings: _*).dependsOn(workloads, datageneration)
