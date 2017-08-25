package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class NotebookSimTest extends FlatSpec with Matchers with BeforeAndAfterEach with Capturing {
  val dataMaker = new BuildAndTeardownData("notebook-sim-test")

  val giantData = s"${dataMaker.sparkBenchTestFolder}/giant-kmeans-data.parquet"
  val tinyData = s"${dataMaker.sparkBenchTestFolder}/tiny-kmeans-data.parquet"

  override def beforeEach(): Unit = {
    super.beforeEach()
    dataMaker.deleteFolders()
    dataMaker.createFolders()
    dataMaker.generateKMeansData(400000, 50, giantData)
    dataMaker.generateKMeansData(100, 5, tinyData)
  }

  override def afterEach(): Unit = {
    dataMaker.deleteFolders()
  }

  "Simulating two notebook users" should "work" in {
    val relativePath = "/etc/notebook-sim.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))
  }



}
