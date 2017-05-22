package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.testfixtures.SparkSessionProvider
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class ConfigFileTest extends FlatSpec with Matchers with BeforeAndAfterAll with Capturing {

  override def beforeAll(): Unit = {
    super.beforeAll()
    BuildAndTeardownData.deleteFiles()
    BuildAndTeardownData.generateKMeansData()
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
