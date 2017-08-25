package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File

import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class SparkLaunchConfTest extends FlatSpec with Matchers with BeforeAndAfter {

  before {
    SparkSession.clearDefaultSession()
    SparkSession.clearActiveSession()
  }

  "SparkLaunchConf" should "turn into arguments properly" in {

    val relativePath = "/etc/sparkConfTest.conf"
    val resource = new File(getClass.getResource(relativePath).toURI)
//    val source = scala.io.Source.fromFile(resource)
    val (sparkContextConfs, _) = SparkLaunch.mkConfs(resource)
    val conf1 = sparkContextConfs.head._1

    val expectedSparkConfs = Array(
      "--conf", "spark.shuffle.service.enabled=false",
      "--conf", "spark.fake=yes",
      "--conf", "spark.dynamicAllocation.enabled=false"
    )

    conf1.sparkConfs shouldBe expectedSparkConfs

    SparkLaunch.rmTmpFiles(sparkContextConfs.map(_._2))

//    val resultConf = conf1.createSparkContext().sparkContext.getConf
//    resultConf.getBoolean("spark.dynamicAllocation.enabled", defaultValue = true) shouldBe false
//    resultConf.getBoolean("spark.shuffle.service.enabled", defaultValue = true) shouldBe false
//    resultConf.get("spark.fake") shouldBe "yes"
  }

  it should "not blow up when spark context confs are left out" in {
    val relativePath = "/etc/testConfFile1.conf"
    val resource = new File(getClass.getResource(relativePath).toURI)
    val (sparkContextConfs, _) = SparkLaunch.mkConfs(resource)
    val conf2 = sparkContextConfs.head._1

    conf2.sparkConfs.isEmpty shouldBe true

    SparkLaunch.rmTmpFiles(sparkContextConfs.map(_._2))
  }

}
