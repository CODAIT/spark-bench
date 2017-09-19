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

package com.ibm.sparktc.sparkbench.workload.ml

import com.ibm.sparktc.sparkbench.testfixtures.SparkSessionProvider
import com.ibm.sparktc.sparkbench.workload.ConfigCreator
import org.scalatest.{FlatSpec, Matchers}

class LogisticRegressionWorkloadTest extends FlatSpec with Matchers {
  private implicit val spark = SparkSessionProvider.spark

  private val cfg = Map(
    "name" -> "lr-bml",
    "input" -> "cli/src/test/resources/lr-bml/lr-train.csv",
    "testfile" -> "cli/src/test/resources/lr-bml/lr-test.csv"
  )

  private var lr: LogisticRegressionWorkload = _
  private val input = s"${cfg("input")}"
  private val testFile = s"${cfg("testfile")}"

  "ConfigCreator" should "create lr-bml" in {
    val workload = ConfigCreator.mapToConf(cfg)
    workload shouldBe a [LogisticRegressionWorkload]
    lr = workload.asInstanceOf[LogisticRegressionWorkload]
    lr.input shouldBe cfg.get("input")
    lr.testFile shouldBe cfg("testfile")
    lr.cacheEnabled shouldBe true
    lr.numPartitions shouldBe 32
  }

  "LogisticRegressionWorkload" should "load training file" in {
    val dtrain = lr.load(input)
    dtrain.count shouldBe 10
  }

  it should "load the test file" in {
    val dtest = lr.load(testFile)
    dtest.count shouldBe 100
  }

  "the ld method" should "split into 32 partitions by default" in {
    val (_, ds) = lr.ld(testFile)
    ds.rdd.getNumPartitions shouldBe 32
  }

  it should "partition accordingly" in {
    val ncfg = cfg ++ Map("numpartitions" -> 48)
    val workload = ConfigCreator.mapToConf(ncfg).asInstanceOf[LogisticRegressionWorkload]
    val (_, ds) = workload.ld(testFile)
    ds.rdd.getNumPartitions shouldBe 48
  }

  it should "cache by default" in {
    val (_, ds) = lr.ld(input)
    ds.storageLevel.useMemory shouldBe true
  }

  it should "disable caching" in {
    val ncfg = cfg ++ Map("cacheenabled" -> false)
    val workload = ConfigCreator.mapToConf(ncfg).asInstanceOf[LogisticRegressionWorkload]
    val (_, ds) = workload.ld(input)
    ds.storageLevel.useMemory shouldBe false
  }

  it should "enable caching" in {
    val ncfg = cfg ++ Map("cacheenabled" -> true)
    val workload = ConfigCreator.mapToConf(ncfg).asInstanceOf[LogisticRegressionWorkload]
    val (_, ds) = workload.ld(input)
    ds.storageLevel.useMemory shouldBe true
  }

  "doWorkload" should "do just that" in {
    val (_, ds) = lr.ld(input)
    val odf = lr.doWorkload(Some(ds), spark)
    odf.count shouldBe 1
    val r = odf.head
    r.getAs[String]("name") shouldBe "lr-bml"
    r.getAs[String]("input") shouldBe input
    r.getAs[String]("test_file") shouldBe testFile
    r.getAs[Long]("train_count") shouldBe 10L
    r.getAs[Long]("test_count") shouldBe 100L
    r.getAs[Double]("area_under_roc") shouldBe 0.615 +- 0.01
  }
}
