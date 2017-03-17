package com.ibm.sparktc.sparkbench

import java.io.File

import com.ibm.sparktc.sparkbench.datagen.DataGenerationConf
import com.ibm.sparktc.sparkbench.datagen.mlgenerator.{KmeansDataGen, KmeansDataGenDefaults}

import scala.io.Source

class KMeansDataGenTest extends UnitSpec {

  val fileName = s"/tmp/kmeans/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  override def beforeAll() {
    file = new File(fileName)
  }

  override def afterAll() {
    if(file.exists()) file.delete()
  }

  "KMeansDataGeneration" should "generate data correctly" in {

    val x = DataGenerationConf(
      generatorName = "kmeans",
      numRows = 10,
      numCols = 10,
      outputFormat = "csv",
      outputDir = fileName,
      generatorSpecific = Map.empty
    )

    val generator = new KmeansDataGen(x)

    generator.run()


    val fileList = file.listFiles().toList.filter(_.getName.startsWith("part"))

    val fileContents: List[String] = fileList
      .flatMap(
        Source.fromFile(_)
          .getLines()
          .toList
      )

    val length: Int = fileContents.length

    length shouldBe x.numRows
  }

  it should "handle an empty map well enough" in {
    val x = DataGenerationConf(
      generatorName = "kmeans",
      numRows = 10,
      numCols = 10,
      outputFormat = "csv",
      outputDir = fileName,
      generatorSpecific = Map.empty
    )

    val generator = new KmeansDataGen(x)

    generator.numPar shouldBe KmeansDataGenDefaults.NUM_OF_PARTITIONS


  }

}