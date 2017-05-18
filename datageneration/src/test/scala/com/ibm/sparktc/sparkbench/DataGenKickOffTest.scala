package com.ibm.sparktc.sparkbench

import java.io.File

import com.holdenkarau.spark.testing.{DataFrameSuiteBase, Utils}
import com.ibm.sparktc.sparkbench.datageneration.{DataGenerationConf, DataGenerationKickoff}
import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class DataGenKickOffTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val filename = s"${BuildAndTeardownData.inputFolder}/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  override def beforeEach() {
    file = new File(filename)
  }

  override def afterEach() {
    BuildAndTeardownData.deleteFiles()
  }

  "DataGenKickOff" should "throw an error if it doesn't recognize an input" in {
    val conf: DataGenerationConf = DataGenerationConf(
      generatorName = "not a legit generator name",
      numRows = 1,
      numCols = 1,
      outputDir = "whatever",
      outputFormat = None,
      generatorSpecific = Map.empty
    )

    an[Exception] shouldBe thrownBy(DataGenerationKickoff.apply(conf))
  }

  it should "not throw an error if the generator name is legit" in {
    val conf: DataGenerationConf = DataGenerationConf(
      generatorName = "kmeans",
      numRows = 1,
      numCols = 1,
      outputDir = filename,
      outputFormat = None,
      generatorSpecific = Map.empty
    )

    DataGenerationKickoff.apply(conf) // if this errors, it'll fail the test
  }
}
