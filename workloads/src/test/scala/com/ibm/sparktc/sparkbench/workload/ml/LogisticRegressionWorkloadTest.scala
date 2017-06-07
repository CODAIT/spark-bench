package com.ibm.sparktc.sparkbench.workload.ml

import com.ibm.sparktc.sparkbench.testfixtures.SparkSessionProvider
import com.ibm.sparktc.sparkbench.workload.ConfigCreator
import org.scalatest.{FlatSpec, Matchers}

class LogisticRegressionWorkloadTest extends FlatSpec with Matchers {

  val spark = SparkSessionProvider.spark

  private val cfg = Map(
    "name" -> "lr-bml",
    "input" -> "workloads/src/test/resources/lr-bml",
    "trainfile" -> "lr-train.csv",
    "testfile" -> "lr-test.csv"
  )

  private var lr: LogisticRegressionWorkload = _
  private val trainFile = s"${cfg("input")}/${cfg("trainfile")}"
  private val testFile = s"${cfg("input")}/${cfg("testfile")}"

  "ConfigCreator" should "create lr-bml" in {
    val workload = ConfigCreator.mapToConf(cfg)
    workload shouldBe a [LogisticRegressionWorkload]
    lr = workload.asInstanceOf[LogisticRegressionWorkload]
    lr.name shouldBe cfg("name")
    lr.inputDir shouldBe cfg.get("input")
    lr.trainFile shouldBe cfg("trainfile")
    lr.testFile shouldBe cfg("testfile")
    lr.cacheEnabled shouldBe true
    lr.numPartitions shouldBe 32
  }

  "LogisticRegressionWorkload" should "load training file" in {
    val dtrain = lr.load(trainFile)(spark)
    dtrain.count shouldBe 100
  }

  it should "load the test file" in {
    val dtest = lr.load(testFile)(spark)
    dtest.count shouldBe 100
  }

  "the ld method" should "split into 32 partitions by default" in {
    val (_, ds) = lr.ld(trainFile)(spark)
    ds.rdd.getNumPartitions shouldBe 32
  }

  it should "partition accordingly" in {
    val ncfg = cfg ++ Map("numpartitions" -> 48)
    val workload = ConfigCreator.mapToConf(ncfg).asInstanceOf[LogisticRegressionWorkload]
    val (_, ds) = workload.ld(trainFile)(spark)
    ds.rdd.getNumPartitions shouldBe 48
  }

  it should "cache by default" in {
    val (_, ds) = lr.ld(trainFile)(spark)
    ds.storageLevel.useMemory shouldBe true
  }

  it should "disable caching" in {
    val ncfg = cfg ++ Map("cacheenabled" -> false)
    val workload = ConfigCreator.mapToConf(ncfg).asInstanceOf[LogisticRegressionWorkload]
    val (_, ds) = workload.ld(trainFile)(spark)
    ds.storageLevel.useMemory shouldBe false
  }

  it should "enable caching" in {
    val ncfg = cfg ++ Map("cacheenabled" -> true)
    val workload = ConfigCreator.mapToConf(ncfg).asInstanceOf[LogisticRegressionWorkload]
    val (_, ds) = workload.ld(trainFile)(spark)
    ds.storageLevel.useMemory shouldBe true
  }

  "doWorkload" should "do just that" in {
    val (_, ds) = lr.ld(trainFile)(spark)
    val odf = lr.doWorkload(Some(ds), spark)
    odf.count shouldBe 1
    val r = odf.head
    r.getAs[String]("name") shouldBe "lr-bml"
    r.getAs[String]("train_file") shouldBe "lr-train.csv"
    r.getAs[String]("test_file") shouldBe "lr-test.csv"
    r.getAs[Long]("train_count") shouldBe 100L
    r.getAs[Long]("test_count") shouldBe 100L
    r.getAs[Double]("area_under_roc") shouldBe 0.99 +- 0.01
  }
}