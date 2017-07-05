package com.ibm.sparktc.sparkbench.workload.ml

import java.io.File

import com.holdenkarau.spark.testing.{DataFrameSuiteBase, Utils}
import com.ibm.sparktc.sparkbench.testfixtures.SparkSessionProvider
import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.{load, writeToDisk}
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class KMeansWorkloadTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val spark = SparkSessionProvider.spark
  val fileName = s"/tmp/spark-bench-scalatest/kmeans-${java.util.UUID.randomUUID.toString}.csv"

  override def afterEach() {
    Utils.deleteRecursively(new File(fileName))
  }

  def makeDataFrame(): DataFrame = {
    val data: RDD[Array[Double]] = KMeansDataGenerator.generateKMeansRDD(
      spark.sparkContext, 1, 1, 1, KMeansDefaults.SCALING, KMeansDefaults.NUM_OF_PARTITIONS
    )
    val schemaString = data.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, DoubleType, nullable = false))
    val schema = StructType(fields)
    val rowRDD = data.map(arr => Row(arr: _*))
    spark.createDataFrame(rowRDD, schema)
  }

  "reconcileSchema" should "handle a StringType schema and turn it into a DoubleType Schema" in {
    val df2Disk = makeDataFrame()
    writeToDisk(fileName, df2Disk, spark, Some("csv"))
    val conf = Map("name" -> "kmeans", "input" -> fileName)
    val work = new KMeansWorkload(conf)
    val df = load(spark, fileName)
    val ddf = work.reconcileSchema(df)
    ddf.schema.head.dataType shouldBe DoubleType
  }

  "The load function" should "parse the DataFrame it's given into an RDD[Vector]" in {
    val df = makeDataFrame()
    val conf = Map("name" -> "kmeans", "input" -> "")
    val work = new KMeansWorkload(conf)
    val ddf = work.reconcileSchema(df)
    val (_, rdd) = work.loadToCache(ddf, spark)
    rdd.first()
  }

  it should "work even when we've pulled the data from disk" in {
    val df2Disk = makeDataFrame()
    writeToDisk(fileName, df2Disk, spark, Some("csv"))
    val conf = Map("name" -> "kmeans", "input" -> fileName)
    val work = new KMeansWorkload(conf)
    val df = load(spark, fileName)
    val ddf = work.reconcileSchema(df)
    val (_, rdd) = work.loadToCache(ddf, spark)
    rdd.first()
  }

  "doWorkload" should "work" in {
    val df2Disk = makeDataFrame()
    writeToDisk(fileName, df2Disk, spark, Some("csv"))
    val conf = Map("name" -> "kmeans", "input" -> fileName)
    val work = new KMeansWorkload(conf)
    val df = load(spark, fileName)
    val ddf = work.reconcileSchema(df)
    work.doWorkload(Some(ddf), spark)
  }
}
