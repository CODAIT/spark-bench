package com.ibm.sparktc.sparkbench

import java.io.File

import com.holdenkarau.spark.testing.{DataFrameSuiteBase, Utils}
import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.datageneration.DataGenerationConf
import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.KMeansDataGen
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class ConfigFileTest extends FlatSpec with Matchers with BeforeAndAfterAll with DataFrameSuiteBase {

  val inputFile = "/tmp/whatever.parquet"

  override def beforeAll(): Unit = {
    super.beforeAll()

    Utils.deleteRecursively(new File(inputFile))

    val conf = DataGenerationConf(
      generatorName = "kmeans",
      numRows = 100,
      numCols = 10,
      outputDir = inputFile,
      outputFormat = None,
      generatorSpecific = Map[String, Any]()
    )
    val kMeansDataGen = new KMeansDataGen(conf, spark)

    val df = kMeansDataGen.generateData(spark)

    writeToDisk(conf.outputDir, df, None, spark)
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
