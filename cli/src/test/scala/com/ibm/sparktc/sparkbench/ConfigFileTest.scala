package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class ConfigFileTest extends FlatSpec with Matchers with BeforeAndAfterEach with Capturing {

  val dataShiznit = new BuildAndTeardownData("tmp")

  override def beforeEach(): Unit = {
    super.beforeEach()
    dataShiznit.deleteFolders()
    dataShiznit.createFolders()
    dataShiznit.generateKMeansData(1000, 5, dataShiznit.kmeansFile)
  }

  override def afterEach(): Unit = {
    dataShiznit.deleteFolders()
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
