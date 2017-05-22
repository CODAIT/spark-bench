package com.ibm.sparktc.sparkbench.datageneration

import java.io.File

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.LinearRegressionDataGen
import com.ibm.sparktc.sparkbench.testfixtures.{BuildAndTeardownData, SparkSessionProvider}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.load
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class LinearRegDataGenTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val cool = new BuildAndTeardownData("linear-reg-datagen")
  
  val fileName = s"${cool.sparkBenchTestFolder}/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  override def beforeEach() {
    cool.deleteFolders()
    file = new File(fileName)
    cool.createFolders()
  }

  override def afterEach() {
    cool.deleteFolders()
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