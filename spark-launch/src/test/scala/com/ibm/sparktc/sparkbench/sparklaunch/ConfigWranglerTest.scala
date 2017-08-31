package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File

import com.typesafe.config.{ConfigFactory, Config}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import com.ibm.sparktc.sparkbench.sparklaunch.{SparkLaunchDefaults => SLD}


class ConfigWranglerTest extends FlatSpec with Matchers with BeforeAndAfterEach {

  def relativeResource(str: String): File = {
    val resource = getClass.getResource(str)
    new File(resource.getPath)
  }

  val bigConf = relativeResource("/etc/configWrangler1.conf")
  val moreMinimalConf = relativeResource("/etc/configWrangler2.conf")
  val toConf: (File) => Config = ConfigFactory.parseFile

  override def beforeEach() {

  }

  override def afterEach() {

  }

  behavior of "ConfigWranglerTest"

  it should "getListOfSparkSubmits" in {
    val seq: Seq[Config] = ConfigWrangler.getListOfSparkSubmits(toConf(bigConf).getObject(SLD.topLevelConfObject).toConfig)
    seq.size shouldBe 2

    val seq2: Seq[Config] = ConfigWrangler.getListOfSparkSubmits(toConf(moreMinimalConf).getObject(SLD.topLevelConfObject).toConfig)
    seq2.size shouldBe 1
  }

  it should "Process configs into case classes and back" in {
    val seq1: Seq[Config] = ConfigWrangler.getListOfSparkSubmits(toConf(bigConf).getObject(SLD.topLevelConfObject).toConfig)
    val seq2: Seq[Config] = ConfigWrangler.getListOfSparkSubmits(toConf(moreMinimalConf).getObject(SLD.topLevelConfObject).toConfig)

    val res1 = seq1.flatMap(ConfigWrangler.processConfig)
    val res2 = seq2.flatMap(ConfigWrangler.processConfig)

    //TODO ugh, this exposed a bug. Well, I guess that's what tests are supposed to do...
    res1.size shouldBe 5 // 4 resulting from crossjoin plus 1 more from other spark-submit-config
    res2.size shouldBe 1
  }
}
