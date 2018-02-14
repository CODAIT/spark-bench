/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

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
    "org.apache.spark" %% "spark-hive"  % sparkVersion % "provided",
    "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-sql"   % sparkVersion % "provided",
    "com.databricks"   %% "spark-avro"  % "4.0.0"
  )

  val breezeDeps = Seq(
    "org.scalanlp" % "breeze_2.11" % "0.13.2"
  )

  val jsonCreation = Seq(
    "com.softwaremill.sttp" %% "json4s" % "1.1.3",
    "com.softwaremill.sttp" %% "core" % "1.1.3"
  )

  val typesafe = Seq(
    "com.typesafe" % "config" % "1.3.1"
  )

  val otherCompileDeps = Seq(
  )

  val testDeps = Seq(
    "junit"             % "junit"              % junitVersion       % "test",
    "org.scalacheck"   %% "scalacheck"         % scalacheckVersion  % "test",
    "org.scalactic"    %% "scalactic"          % scalatestVersion   % "test",
    "org.scalatest"    %% "scalatest"          % scalatestVersion   % "test",
    "org.apache.spark" %% "spark-hive"         % sparkVersion       % "test",
    "com.holdenkarau"  %% "spark-testing-base" % "2.1.1_0.7.2"      % "test" excludeAll(
      ExclusionRule(organization = "org.scalacheck"),
      ExclusionRule(organization = "org.scalactic"),
      ExclusionRule(organization = "org.scalatest"),
      ExclusionRule(organization = "org.scala-lang")
    )
  )
}