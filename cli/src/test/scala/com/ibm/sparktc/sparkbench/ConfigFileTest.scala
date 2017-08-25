package com.ibm.sparktc.sparkbench

import java.io.File

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.io.Source

class ConfigFileTest extends FlatSpec with Matchers with BeforeAndAfterAll with Capturing {

  val dataShiznit = new BuildAndTeardownData("configfiletest")

  override def beforeAll(): Unit = {
    super.beforeAll()
    dataShiznit.deleteFolders()
    dataShiznit.createFolders()
  }

  override def afterAll(): Unit = {
    dataShiznit.deleteFolders()
  }

  val kmeansData = new File("/tmp/spark-bench-scalatest/configfiletest/kmeans-data.parquet")
  val output1 = new File("/tmp/spark-bench-scalatest/configfiletest/conf-file-output-1.csv")
  val output2 = new File("/tmp/spark-bench-scalatest/configfiletest/conf-file-output-2.parquet")

  "Spark-bench run through a config file serially" should "work" in {
    kmeansData.exists() shouldBe false

    val relativePath = "/etc/testConfFile1.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))

    kmeansData.exists() shouldBe true
    output1.exists() shouldBe true
    output2.exists() shouldBe true

    val fileList = output1.listFiles().toList.filter(_.getName.startsWith("part"))

    val fileContents: List[String] =
        Source.fromFile(fileList.head)
          .getLines()
          .toList


    val length: Int = fileContents.length

    (length > 0) shouldBe true
  }

  "Spark-bench run through a config file with the suites running in parallel" should "work" in {
    kmeansData.exists() shouldBe true
    val relativePath = "/etc/testConfFile2.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))
  }
}
