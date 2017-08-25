import sbt._

object Dependencies {
  // Versions
  lazy val sparkVersion = "2.1.1"
  lazy val scalacheckVersion = "1.13.4"
  lazy val junitVersion = "4.12"
  lazy val scalatestVersion = "3.0.1"

  // Libraries
  val sparkDeps = Seq(
    "org.apache.spark" %% "spark-core"  % sparkVersion % "provided",
    "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-sql"   % sparkVersion % "provided"
  )

  val otherCompileDeps = Seq(
//    "org.jblas" % "jblas" % "1.2.4"
  )

  val testDeps = Seq(
    "junit"             % "junit"              % junitVersion       % "test",
    "org.scalacheck"   %% "scalacheck"         % scalacheckVersion  % "test",
    "org.scalactic"    %% "scalactic"          % scalatestVersion   % "test",
    "org.scalatest"    %% "scalatest"          % scalatestVersion   % "test",
    "org.apache.spark" %% "spark-hive"         % sparkVersion       % "test",
    "com.holdenkarau"  %% "spark-testing-base" % "2.1.0_0.6.0"      % "test" excludeAll(
      ExclusionRule(organization = "org.scalacheck"),
      ExclusionRule(organization = "org.scalactic"),
      ExclusionRule(organization = "org.scalatest"),
      ExclusionRule(organization = "org.scala-lang")
    )
  )

  val typesafe = Seq(
    "com.typesafe" % "config" % "1.3.1"
  )
}