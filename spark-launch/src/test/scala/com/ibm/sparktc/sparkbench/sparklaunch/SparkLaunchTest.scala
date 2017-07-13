package com.ibm.sparktc.sparkbench.sparklaunch

import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

class SparkLaunchTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val dataShiznit = new BuildAndTeardownData("multi-spark")

  override def beforeEach(): Unit = {
    super.beforeEach()
    dataShiznit.deleteFolders()
    dataShiznit.createFolders()
    dataShiznit.generateKMeansData(1000, 5, dataShiznit.kmeansFile)
  }

  override def afterEach(): Unit = {
    dataShiznit.deleteFolders()
  }
  "Launching Spark" should "work" in {
    val relativePath = "/etc/testConfFile1.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    SparkLaunch.main(Array(path))
  }
}
