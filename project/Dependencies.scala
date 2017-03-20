import sbt._

object Dependencies {
  // Versions
  lazy val sparkVersion = "2.1.0"
  lazy val scalacheckVersion = "1.11.4"
  lazy val junitVersion = "4.12"
  lazy val scalatestVersion = "2.2.4"

  // Libraries
  val sparkDeps = Seq(
    "org.apache.spark"            %% "spark-core"            % sparkVersion           % "provided",
    "org.apache.spark"            %% "spark-mllib"           % sparkVersion           % "provided")

  val otherCompileDeps = Seq(
    "org.jblas"                    % "jblas"                 % "1.2.4"
  )

  val testDeps = Seq(
    "junit"                       % "junit"                  % junitVersion            % "test",
    "org.scalacheck"              %% "scalacheck"            % scalacheckVersion       % "test",
    "org.scalactic"               %% "scalactic"             % scalatestVersion        % "test",
    "org.scalatest"               %% "scalatest"             % scalatestVersion        % "test"
  )
//  // Projects
//  val mostProjects =
//    Seq(sparkDeps, otherCompileDeps, testDeps)
}