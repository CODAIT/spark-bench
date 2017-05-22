package com.ibm.sparktc.sparkbench.datageneration

import java.io.File

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.{KMeansDataGen, LinearRegressionDataGen}
import com.ibm.sparktc.sparkbench.testfixtures.{BuildAndTeardownData, SparkSessionProvider}
import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

import scala.io.Source

class KMeansDataGenTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val cool = new BuildAndTeardownData("kmeans-data-gen")
  
  val fileName = s"${cool.sparkBenchTestFolder}/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  override def beforeEach() {
    cool.deleteFolders()
    cool.createFolders()
    file = new File(fileName)
  }

  override def afterEach() {
    cool.deleteFolders()
  }

  "KMeansDataGeneration" should "generate data correctly" in {

    val x = DataGenerationConf(
      generatorName = "kmeans",
      numRows = 10,
      numCols = 10,
      outputFormat = Some("csv"),
      outputDir = fileName,
      generatorSpecific = Map.empty
    )

    val generator = new KMeansDataGen(x, SparkSessionProvider.spark)

    generator.run()


    val fileList = file.listFiles().toList.filter(_.getName.startsWith("part"))

    val fileContents: List[String] = fileList
      .flatMap(
        Source.fromFile(_)
          .getLines()
          .toList
      )

    val length: Int = fileContents.length

    /*
    *  Okay, some explanation here. I made headers default for csv, so there's going to be
    *  one extra header line per partition file. If the csv header option ever changes, this
    *  test will break, but now you know what's going on so you can fix it :)
    */
    length shouldBe x.numRows + fileList.length
  }

  it should "handle an empty map well enough" in {
    val x = DataGenerationConf(
      generatorName = "kmeans",
      numRows = 10,
      numCols = 10,
      outputFormat = Some("csv"),
      outputDir = fileName,
      generatorSpecific = Map.empty
    )

    val generator = new KMeansDataGen(x, SparkSessionProvider.spark)

    generator.numPar shouldBe KMeansDefaults.NUM_OF_PARTITIONS
  }

}