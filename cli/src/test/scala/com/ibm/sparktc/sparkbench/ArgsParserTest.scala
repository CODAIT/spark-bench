package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.{ArgsParser, ScallopArgs}
import com.ibm.sparktc.sparkbench.datagen.mlgenerator.KmeansDataGenDefaults
import org.scalatest.{FlatSpec, Matchers}

class ArgsParserTest extends FlatSpec with Matchers {

  "Correct KMeans Datagen Args" should "parse to a DataGenConf" in {
    val sArgs = new ScallopArgs(
      Array("generate-data", "-r", "100", "-c", "10", "-o", "/tmp/cool", "--output-format", "csv", "kmeans")
    )

    val conf = ArgsParser.parseDataGen(sArgs)

    println(conf)

    conf.numRows shouldBe 100
    conf.outputDir shouldBe "/tmp/cool"
    conf.outputFormat shouldBe "csv"
    conf.generatorName shouldBe "kmeans"
    conf.generatorSpecific shouldBe Map(
      "k" -> KmeansDataGenDefaults.NUM_OF_CLUSTERS,
      "scaling" -> KmeansDataGenDefaults.SCALING,
      "partitions" -> KmeansDataGenDefaults.NUM_OF_PARTITIONS
    )

  }

}
