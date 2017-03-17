package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.{ArgsParser, ScallopArgs}
import org.scalatest.{FlatSpec, Matchers}

class ArgsParserTest extends FlatSpec with Matchers {

  "Correct KMeans Datagen Args" should "parse to a DataGenConf" in {
    val sArgs = new ScallopArgs(Array("generate-data", "-r", "100", "kmeans"))

    val conf = ArgsParser.parseDataGen(sArgs)

    conf.numRows shouldBe 100
    conf.outputFormat shouldBe ""
    conf.outputDir shouldBe ""
    conf.generatorName shouldBe "kmeans"
    conf.generatorSpecific shouldBe Map(
      "k" -> 1,
      "maxIterations" -> 1,
      "seed" -> 1
    )

  }

}
