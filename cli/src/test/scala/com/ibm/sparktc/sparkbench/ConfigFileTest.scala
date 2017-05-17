package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.testfixtures.{BuildAndTeardownData, SparkSessionProvider}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

class ConfigFileTest extends FlatSpec with Matchers with BeforeAndAfterEach with Capturing {

  override def beforeEach(): Unit = {
    super.beforeEach()
    BuildAndTeardownData.deleteFiles()
    BuildAndTeardownData.generateKMeansData(1000, 5, BuildAndTeardownData.inputFile)
  }

  override def afterEach(): Unit = {
    BuildAndTeardownData.deleteFiles()
  }

  "Spark-bench run through a config file serially" should "work" in {
    val relativePath = "/etc/testConfFile1.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))
  }

  "Spark-bench run through a config file with the suites running in parallel" should "work" in {
    val relativePath = "/etc/testConfFile2.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))
  }


}
