package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.{ArgsParser, ScallopArgs}
import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
import com.ibm.sparktc.sparkbench.utils.test.UnitSpec

class ArgsParserTest extends UnitSpec {

  "Correct KMeans Datagen Args" should "parse to a DataGenConf" in {
    val sArgs = new ScallopArgs(
      Array("generate-data", "-r", "100", "-c", "10", "-o", "/tmp/cool", "--output-format", "csv", "kmeans")
    )

    val conf = ArgsParser.parseDataGen(sArgs)

    println(conf)

    conf.numRows shouldBe 100
    conf.outputDir shouldBe "/tmp/cool"
    conf.outputFormat shouldBe Some("csv")
    conf.generatorName shouldBe "kmeans"
    conf.generatorSpecific shouldBe Map(
      "k" -> KMeansDefaults.NUM_OF_CLUSTERS,
      "scaling" -> KMeansDefaults.SCALING,
      "partitions" -> KMeansDefaults.NUM_OF_PARTITIONS
    )

  }

}
