package com.ibm.sparktc.sparkbench.datageneration

import java.io.File

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.LinearRegressionDataGen
import com.ibm.sparktc.sparkbench.testfixtures.{BuildAndTeardownData, SparkSessionProvider}
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.load
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class LinearRegDataGenTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val cool = new BuildAndTeardownData("linear-reg-datagen")

  val fileName = s"${cool.sparkBenchTestFolder}/${java.util.UUID.randomUUID.toString}.parquet"
  val csvFileName = s"${cool.sparkBenchTestFolder}/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  override def beforeEach() {
    file = new File(fileName)
    cool.createFolders()
  }

  override def afterEach() {
    cool.deleteFolders()
  }

  "LinearRegressionDataGen" should "generate data correctly for Parquet output" in {

    val m = Map(
      "name" -> "kmeans",
      "rows" -> 10,
      "cols" -> 10,
      "output" -> fileName
    )

    val generator = LinearRegressionDataGen(m)

    generator.doWorkload(spark = SparkSessionProvider.spark)

    val fileList = file.listFiles().toList.filter(_.getName.startsWith("part"))
    val fileContents = load(SparkSessionProvider.spark, fileName, Some("parquet"))
    val length: Long = fileContents.count()

    length shouldBe generator.numRows
  }

  //TODO ultimately fix LabeledPoints being output to CSV. Surely there's a way...
  it should "throw an exception when somebody tries to output to CSV" in {
    a [SparkBenchException] should be thrownBy {
      val m = Map(
        "name" -> "kmeans",
        "rows" -> 10,
        "cols" -> 10,
        "output" -> csvFileName
      )
      val generator = LinearRegressionDataGen(m)
      generator.doWorkload(spark = SparkSessionProvider.spark)
    }
  }

}