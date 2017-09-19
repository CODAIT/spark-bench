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

import java.io.File

import com.holdenkarau.spark.testing.Utils
import com.ibm.sparktc.sparkbench.testfixtures.SparkSessionProvider
import com.ibm.sparktc.sparkbench.workload.ml.KMeansWorkload
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
      spark.sparkContext, 1, 1, 1, KMeansWorkload.scaling, KMeansWorkload.numOfPartitions
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
    val work = KMeansWorkload(conf)
    val df = load(spark, fileName)
    val ddf = work.reconcileSchema(df)
    ddf.schema.head.dataType shouldBe DoubleType
  }

  "The load function" should "parse the DataFrame it's given into an RDD[Vector]" in {
    val df = makeDataFrame()
    val conf = Map("name" -> "kmeans", "input" -> "")
    val work = KMeansWorkload(conf)
    val ddf = work.reconcileSchema(df)
    val (_, rdd) = work.loadToCache(ddf, spark)
    rdd.first()
  }

  it should "work even when we've pulled the data from disk" in {
    val df2Disk = makeDataFrame()
    writeToDisk(fileName, df2Disk, spark, Some("csv"))
    val conf = Map("name" -> "kmeans", "input" -> fileName)
    val work = KMeansWorkload(conf)
    val df = load(spark, fileName)
    val ddf = work.reconcileSchema(df)
    val (_, rdd) = work.loadToCache(ddf, spark)
    rdd.first()
  }

  "doWorkload" should "work" in {
    val df2Disk = makeDataFrame()
    writeToDisk(fileName, df2Disk, spark, Some("csv"))
    val conf = Map("name" -> "kmeans", "input" -> fileName)
    val work = KMeansWorkload(conf)
    val df = load(spark, fileName)
    val ddf = work.reconcileSchema(df)
    work.doWorkload(Some(ddf), spark)
  }
}
