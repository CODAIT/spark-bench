/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File

import com.ibm.sparktc.sparkbench.sparklaunch.confparse.ConfigWrangler
import com.typesafe.config.{Config, ConfigFactory}
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

    res1.size shouldBe 5 // 4 resulting from crossjoin plus 1 more from other spark-submit-config
    res2.size shouldBe 1
  }
}
