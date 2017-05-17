package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class NotebookSimTest extends FlatSpec with Matchers with BeforeAndAfterEach with Capturing {

  override def beforeEach(): Unit = {
    super.beforeEach()
    BuildAndTeardownData.deleteFiles()
    BuildAndTeardownData.generateKMeansData(400000, 50, s"${BuildAndTeardownData.inputFolder}/giant-kmeans-data.parquet")
    BuildAndTeardownData.generateKMeansData(100, 5, s"${BuildAndTeardownData.inputFolder}/tiny-kmeans-data.parquet")
  }

  override def afterEach(): Unit = {
    BuildAndTeardownData.deleteFiles()
  }

  "Simulating two notebook users" should "work" in {
    val relativePath = "/etc/notebook-sim.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))
  }



}
