package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class OutputTest extends FlatSpec with Matchers with BeforeAndAfterAll with Capturing {
  val dataStuff = new BuildAndTeardownData("output-test")

  override def beforeAll(): Unit = {
    super.beforeAll()
    dataStuff.createFolders()
    dataStuff.generateKMeansData(1000, 5, dataStuff.kmeansFile)
  }

  override def afterAll(): Unit = {
    dataStuff.deleteFolders()
    super.afterAll()
  }

  "Specifying Console output" should "work" in {
    val (out) = captureOutput(CLIKickoff.main(
      Array("bin/spark-bench.sh", "workload", "kmeans", "-i", dataStuff.kmeansFile, "-o", "console", "-n", "5", "-k", "2"))
    )
    println(out)
  }


  "Want to see configuration added to results when there's crazy stuff" should "work" in {
    val (out) = captureOutput(CLIKickoff.main(
      Array("bin/spark-bench.sh", "workload", "kmeans", "-i", dataStuff.kmeansFile, "-o", s"${dataStuff.sparkBenchDemoFolder}/test-results5.csv",
        "--parallel", "-n", "5", "-k", "2", "32", "-e", "127", "1093"))
    )
    println(out)
  }
}
