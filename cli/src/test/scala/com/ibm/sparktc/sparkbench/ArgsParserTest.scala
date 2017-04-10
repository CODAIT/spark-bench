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

  "Correct KMeans Workload Args" should "parse to a WorkloadConf" in {
    val sArgs = new ScallopArgs(
      Array("workload", "-i", "/tmp/coolstuff1", "/tmp/coolstuff2", "-o", "~/Desktop/test-results/", "kmeans", "-k", "2", "32")
    )

    val conf = ArgsParser.parseWorkload(sArgs)

    println(conf)

    conf.outputDir shouldBe "~/Desktop/test-results/"
    conf.runs shouldBe 1 // default
    conf.parallel shouldBe false // default
    conf.inputDir shouldBe Seq("/tmp/coolstuff1", "/tmp/coolstuff2")
    conf.workloadResultsOutputDir shouldBe None // default
    conf.workloadSpecific shouldBe Map(
      "k" -> Seq(2, 32),
      "maxIterations" -> Seq(2), // default
      "seed" -> Seq(127L) // default
    )
  }

}
