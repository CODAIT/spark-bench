package com.ibm.sparktc.sparkbench

import java.io.File

import com.ibm.sparktc.sparkbench.cli.Configurator
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class ConfiguratorSpec extends FlatSpec with Matchers with BeforeAndAfter {

  before {
    SparkSession.clearDefaultSession()
    SparkSession.clearActiveSession()
  }

  "Configurator" should "get spark configs from config file" in {

    val relativePath = "/etc/sparkConfTest.conf"
    val resource = new File(getClass.getResource(relativePath).toURI)
    val source = scala.io.Source.fromFile(resource)
    val sparkContextConf = Configurator(resource).head

    val expectedSparkConfs = Map("spark.dynamicAllocation.enabled" -> "false",
                                 "spark.shuffle.service.enabled" -> "false",
                                 "spark.fake" -> "yes")

    sparkContextConf.sparkConfs shouldBe expectedSparkConfs

    val resultConf = sparkContextConf.createSparkContext().sparkContext.getConf
    resultConf.getBoolean("spark.dynamicAllocation.enabled", defaultValue = true) shouldBe false
    resultConf.getBoolean("spark.shuffle.service.enabled", defaultValue = true) shouldBe false
    resultConf.get("spark.fake") shouldBe "yes"
  }

  it should "not blow up when spark context confs are left out" in {
    val relativePath = "/etc/testConfFile1.conf"
    val resource = new File(getClass.getResource(relativePath).toURI)
    val sparkContextConf = Configurator(resource).head

    sparkContextConf.sparkConfs.isEmpty shouldBe true

  }

}
