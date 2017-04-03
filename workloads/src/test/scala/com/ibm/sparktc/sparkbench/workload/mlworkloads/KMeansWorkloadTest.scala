package com.ibm.sparktc.sparkbench.workload.mlworkloads

import java.io.File

import com.holdenkarau.spark.testing.Utils
import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
import com.ibm.sparktc.sparkbench.workload.WorkloadConfig
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.{load, writeToDisk}
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}


class KMeansWorkloadTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val fileName = s"/tmp/kmeans/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  override def beforeEach() {
//    file = new File(fileName)
  }

  override def afterEach() {
    Utils.deleteRecursively(new File(fileName))
  }

  val spark = SparkSession
    .builder()
    .master("local[1]")
    .getOrCreate()

  def makeDataFrame(): DataFrame = {

    val data: RDD[Array[Double]] = KMeansDataGenerator.generateKMeansRDD(
      spark.sparkContext,
      1,
      1,
      1,
      KMeansDefaults.SCALING,
      KMeansDefaults.NUM_OF_PARTITIONS
    )

    val schemaString = data.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, DoubleType, nullable = false))
    val schema = StructType(fields)

    val rowRDD = data.map(arr => Row(arr: _*))

    spark.createDataFrame(rowRDD, schema)
  }

  "reconcileSchema" should "handle a StringType schema and turn it into a DoubleType Schema" in {
    val df2Disk = makeDataFrame()

    writeToDisk(fileName, df2Disk, Some("csv"))

    val conf = WorkloadConfig(
      name = "kmeans",
      parallel = false,
      runs = 1,
      inputDir = fileName,
//      inputFormat = "csv",
//      workloadResultsOutputFormat = None,
      workloadResultsOutputDir = None,
      outputDir = "",
//      outputFormat = "",
      workloadSpecific = Map.empty
    )

    val work = new KMeansWorkload(conf, sparkSessOpt = Some(spark))

    val df = load(spark, conf.inputDir)

    val ddf = work.reconcileSchema(df)

    ddf.schema.head.dataType shouldBe DoubleType
  }

  "The load function" should "parse the DataFrame it's given into an RDD[Vector]" in {
    val df = makeDataFrame()

    val conf = WorkloadConfig(
      name = "kmeans",
      parallel = false,
      runs = 1,
      inputDir = "",
      workloadResultsOutputDir = None,
      outputDir = "",
      workloadSpecific = Map.empty
    )

    val work = new KMeansWorkload(conf, sparkSessOpt = Some(spark))

    val ddf = work.reconcileSchema(df)
    val (_, rdd) = work.loadToCache(ddf, spark)

    rdd.first()
  }

  it should "work even when we've pulled the data from disk" in {
    val df2Disk = makeDataFrame()
    writeToDisk(fileName, df2Disk, Some("csv"))

    val conf = WorkloadConfig(
      name = "kmeans",
      parallel = false,
      runs = 1,
      inputDir = fileName,
      workloadResultsOutputDir = None,
      outputDir = "",
      workloadSpecific = Map.empty
    )

    val work = new KMeansWorkload(conf, sparkSessOpt = Some(spark))

    val df = load(spark, conf.inputDir)
    val ddf = work.reconcileSchema(df)

    val (_, rdd) = work.loadToCache(ddf, spark)

    rdd.first()
  }

}
