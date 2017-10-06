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

import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class SparkSubmitScriptConfTest extends FlatSpec with Matchers with BeforeAndAfter {

  private def setEnv(key: String, value: String) = {
    val field = System.getenv().getClass.getDeclaredField("m")
    field.setAccessible(true)
    val map = field.get(System.getenv()).asInstanceOf[java.util.Map[java.lang.String, java.lang.String]]
    map.put(key, value)
  }

  private def unsetEnv(key: String) = {
    val field = System.getenv().getClass.getDeclaredField("m")
    field.setAccessible(true)
    val map = field.get(System.getenv()).asInstanceOf[java.util.Map[java.lang.String, java.lang.String]]
    map.remove(key)
  }

  val sparkHome = sys.env("SPARK_HOME")
  val masterHost = "local[2]"

  before {
    SparkSession.clearDefaultSession()
    SparkSession.clearActiveSession()
  }

  after {
    setEnv("SPARK_HOME", sparkHome)
    setEnv("SPARK_MASTER_HOST", masterHost)
  }

  "SparkLaunchConf" should "turn into arguments properly" in {

    val relativePath = "/etc/sparkConfTest.conf"
    val resource = new File(getClass.getResource(relativePath).toURI)
//    val source = scala.io.Source.fromFile(resource)
    val (sparkContextConfs, _) = SparkLaunch.mkConfs(resource)
    val conf1 = sparkContextConfs.head

    val expectedSparkConfs = Array(
      "--conf", "spark.shuffle.service.enabled=false",
      "--conf", "spark.fake=yes",
      "--conf", "spark.dynamicAllocation.enabled=false"
    )

    conf1.sparkConfs shouldBe expectedSparkConfs
    conf1.sparkArgs should contain ("--master")

  }

  it should "not blow up when spark context confs are left out" in {
    val relativePath = "/etc/noMasterConf.conf"
    val oldValue = unsetEnv("SPARK_MASTER_HOST")
    setEnv("SPARK_MASTER_HOST", "local[2]")
    val resource = new File(getClass.getResource(relativePath).toURI)
    val (sparkContextConfs, _) = SparkLaunch.mkConfs(resource)
    val conf2 = sparkContextConfs.head

    conf2.sparkConfs.isEmpty shouldBe true
    conf2.sparkArgs should contain ("--master")
    setEnv("SPARK_MASTER_HOST", masterHost)

  }

  it should "pick up spark-home as set in the config file" in {
    val oldSparkHome = unsetEnv("SPARK_HOME")
    val relativePath = "/etc/specific-spark-home.conf"
    val resource = new File(getClass.getResource(relativePath).toURI)
    val (sparkContextConfs, _) = SparkLaunch.mkConfs(resource)
    val conf2 = sparkContextConfs.head

    conf2.sparkHome shouldBe "/usr/iop/current/spark2-client/"

    setEnv("SPARK_HOME", sparkHome)

  }

}
