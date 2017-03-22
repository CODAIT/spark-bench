package com.ibm.sparktc.sparkbench.workload.mlworkloads

import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
import com.ibm.sparktc.sparkbench.utils.test.UnitSpec
import com.ibm.sparktc.sparkbench.workload.WorkloadConfig
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.{writeToDisk, load}

import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}


class KMeansWorkloadTest extends UnitSpec{

  val spark = SparkSession
    .builder()
    .master("local[2]")
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

  "The load function" should "parse the DataFrame it's given into an RDD[Vector]" in {
    val df = makeDataFrame()

    val conf = WorkloadConfig(
      name = "kmeans",
      inputDir = "",
      inputFormat = "",
      workloadResultsOutputFormat = None,
      workloadResultsOutputDir = None,
      outputDir = "",
      outputFormat = "",
      workloadSpecific = Map.empty
    )

    val work = new KMeansWorkload(conf, sparkSessOpt = Some(spark))

    val (_, rdd) = work.loadToCache(df, spark)

    rdd.first()
  }

  it should "work even when we've pulled the data from disk" in {
    val df2Disk = makeDataFrame()
    val file = "/tmp/spark-bench-test-just-a-test-yes"

    writeToDisk("csv", file, df2Disk)

    val conf = WorkloadConfig(
      name = "kmeans",
      inputDir = file,
      inputFormat = "csv",
      workloadResultsOutputFormat = None,
      workloadResultsOutputDir = None,
      outputDir = "",
      outputFormat = "",
      workloadSpecific = Map.empty
    )

    val work = new KMeansWorkload(conf, sparkSessOpt = Some(spark))

    val df = load(spark, conf.inputFormat, conf.inputDir)
    val (_, rdd) = work.loadToCache(df, spark)

    rdd.first()
  }

}
