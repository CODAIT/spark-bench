package com.ibm.sparktc.sparkbench

import java.io.File

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

import scala.io.Source

class ConfigFileTest extends FlatSpec with Matchers with BeforeAndAfterEach with Capturing {

  val dataShiznit = new BuildAndTeardownData("configfiletest")

  override def beforeEach(): Unit = {
    super.beforeEach()
    dataShiznit.deleteFolders()
    dataShiznit.createFolders()
    // dataShiznit.generateKMeansData(1000, 5, dataShiznit.kmeansFile)
  }

  override def afterEach(): Unit = {
    dataShiznit.deleteFolders()
  }

  "Spark-bench run through a config file serially" should "work" in {
    val relativePath = "/etc/testConfFile1.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))

    val output1 = new File("/tmp/spark-bench-scalatest/configfiletest/conf-file-output-1.csv")
    val output2 = new File("/tmp/spark-bench-scalatest/configfiletest/conf-file-output-2.parquet")

    output1.exists() shouldBe true
    output2.exists() shouldBe true

    val fileList = output1.listFiles().toList.filter(_.getName.startsWith("part"))

    val fileContents: List[String] =
        Source.fromFile(fileList.head)
          .getLines()
          .toList


    val length: Int = fileContents.length
  }

  "Spark-bench run through a config file with the suites running in parallel" should "work" in {
    val relativePath = "/etc/testConfFile2.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))
  }
}
