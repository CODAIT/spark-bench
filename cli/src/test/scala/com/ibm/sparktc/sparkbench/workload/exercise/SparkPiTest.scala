package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.testfixtures.SparkSessionProvider
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class SparkPiTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val spark = SparkSessionProvider.spark

  "SparkPi" should "instantiate and run" in {
    val workload = new SparkPi(Map("name" -> "sparkpi", "slices" -> 4))
    val res = workload.doWorkload(None, spark).collect
    res.length shouldBe 1
    val row = res(0)
    row.length shouldBe 4
    row.getAs[String]("name") shouldBe "sparkpi"
    row.getAs[Double]("pi_approximate") shouldBe 3.14 +- 0.2
  }
}
