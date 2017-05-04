package com.ibm.sparktc.sparkbench

import java.io.File
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import com.holdenkarau.spark.testing.Utils
import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.datageneration.DataGenerationConf
import com.ibm.sparktc.sparkbench.utils.test.UnitSpec
import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.KMeansDataGen

class ConfigFileTest extends UnitSpec with DataFrameSuiteBase {

  val inputFile = "/tmp/whatever.parquet"

  override def beforeAll(): Unit = {
    val conf = DataGenerationConf(
      generatorName = "kmeans",
      numRows = 100,
      numCols = 10,
      outputDir = inputFile,
      outputFormat = None,
      generatorSpecific = Map[String, Any]()
    )
    val kMeansDataGen = new KMeansDataGen(conf, spark)
  }

  override def afterAll(): Unit = {
    Utils.deleteRecursively(new File(inputFile))
  }
  
  "Spark-bench run through a config file" should "work" in {
    val relativePath = "/etc/testConfFile1.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))
  }
}
