package com.ibm.sparktc.sparkbench

import java.io.File

import com.holdenkarau.spark.testing.{DataFrameSuiteBase, Utils}
import com.ibm.sparktc.sparkbench.datageneration.DataGenerationConf
import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.{KMeansDataGen, LinearRegressionDataGen}
import com.ibm.sparktc.sparkbench.testfixtures.{BuildAndTeardownData, SparkSessionProvider}
import com.ibm.sparktc.sparkbench.utils.{KMeansDefaults, LinearRegressionDefaults}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.load
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

import scala.io.Source

class LinearRegDataGenTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val fileName = s"${BuildAndTeardownData.inputFolder}/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  override def beforeEach() {
    file = new File(fileName)
  }

  override def afterEach() {
    BuildAndTeardownData.deleteFiles()
  }

  "LinearRegressionDataGen" should "generate data correctly" in {

    val x = DataGenerationConf(
      generatorName = "linear-regression",
      numRows = 10,
      numCols = 10,
      outputFormat = Some("parquet"),
      outputDir = fileName,
      generatorSpecific = Map.empty
    )

    val generator = new LinearRegressionDataGen(x, SparkSessionProvider.spark)

    generator.run()

    val fileList = file.listFiles().toList.filter(_.getName.startsWith("part"))
    val fileContents = load(SparkSessionProvider.spark, fileName, Some("parquet"))
    val length: Long = fileContents.count()

    length shouldBe x.numRows
  }

}