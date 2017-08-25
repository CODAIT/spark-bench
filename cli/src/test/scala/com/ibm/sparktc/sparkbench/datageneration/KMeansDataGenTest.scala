package com.ibm.sparktc.sparkbench.datageneration

import java.io.File

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.KMeansDataGen
import com.ibm.sparktc.sparkbench.testfixtures.{BuildAndTeardownData, SparkSessionProvider}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

import scala.io.Source

class KMeansDataGenTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val cool = new BuildAndTeardownData("kmeans-data-gen")

  val fileName = s"${cool.sparkBenchTestFolder}/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  override def beforeEach() {
    cool.createFolders()
    file = new File(fileName)
  }

  override def afterEach() {
    cool.deleteFolders()
  }

  "KMeansDataGeneration" should "generate data correctly" in {

    val m = Map(
      "name" -> "kmeans",
      "rows" -> 10,
      "cols" -> 10,
      "output" -> fileName
    )

    val generator = KMeansDataGen(m)


    generator.doWorkload(spark = SparkSessionProvider.spark)


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
    length shouldBe generator.numRows + fileList.length
  }
}