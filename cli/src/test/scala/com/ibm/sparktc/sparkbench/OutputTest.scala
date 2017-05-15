package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class OutputTest extends FlatSpec with Matchers with BeforeAndAfterAll with Capturing {

  override def beforeAll(): Unit = {
    super.beforeAll()

    BuildAndTeardownData.deleteFiles()

    BuildAndTeardownData.generateKMeansData()
  }

  override def afterAll(): Unit = {
    BuildAndTeardownData.deleteFiles()
    super.afterAll()
  }

  "Specifying Console output" should "work" in {
    val (out) = captureOutput(CLIKickoff.main(
      Array("bin/spark-bench.sh", "workload", "kmeans", "-i", BuildAndTeardownData.inputFile, "-o", "console", "-n", "5", "-k", "2"))
    )
    println(out)
  }
}
