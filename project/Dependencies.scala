import sbt._

object Dependencies {
  // Versions
  lazy val sparkVersion = "2.1.0"
  lazy val scalacheckVersion = "1.12.5"
  lazy val junitVersion = "4.12"
  lazy val scalatestVersion = "2.2.6"

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
    "junit"           % "junit"               % junitVersion       % "test",
    "org.scalacheck"  % "scalacheck_2.11"     % scalacheckVersion  % "test",
    "org.scalactic"   % "scalactic_2.11"      % scalatestVersion   % "test",
    "org.scalatest"   % "scalatest_2.11"      % scalatestVersion   % "test",
    "org.apache.spark" %% "spark-hive"       % sparkVersion % "test",
    "com.holdenkarau" %% "spark-testing-base" % "2.1.0_0.6.0"      % "test" excludeAll(
        ExclusionRule(organization = "org.scalacheck"),
        ExclusionRule(organization = "org.scalactic"),
        ExclusionRule(organization = "org.scalatest")
      )
  )

  val typesafe = Seq(
    "com.typesafe" % "config" % "1.3.1"
  )

}