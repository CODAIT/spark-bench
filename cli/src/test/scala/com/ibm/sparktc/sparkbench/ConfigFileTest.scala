package com.ibm.sparktc.sparkbench

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class ConfigFileTest extends FlatSpec with Matchers with BeforeAndAfterAll with DataFrameSuiteBase with Capturing {

  override def beforeAll(): Unit = {
    super.beforeAll()
    BuildAndTeardownData.deleteFiles()
    BuildAndTeardownData.generateKMeansData(spark)
  }

  override def afterAll(): Unit = {
    BuildAndTeardownData.deleteFiles()
  }

  "Spark-bench run through a config file" should "work" in {
    val relativePath = "/etc/testConfFile1.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))
  }


}
