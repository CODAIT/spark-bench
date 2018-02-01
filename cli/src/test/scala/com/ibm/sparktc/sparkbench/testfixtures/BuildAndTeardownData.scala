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

package com.ibm.sparktc.sparkbench.testfixtures

import java.io.File

import com.holdenkarau.spark.testing.Utils
import com.ibm.sparktc.sparkbench.utils.SaveModes
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import com.ibm.sparktc.sparkbench.workload.ml.KMeansWorkload
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

class BuildAndTeardownData(dirname: String = System.currentTimeMillis.toString) {
  val prefix = "/tmp/spark-bench-scalatest/" + dirname
  val sparkBenchTestFolder = s"$prefix/spark-bench-test"
  val kmeansFile = s"$sparkBenchTestFolder/kmeans-data.parquet"
  val sparkBenchDemoFolder = s"$prefix/spark-bench-demo"
  val spark = SparkSessionProvider.spark

  def createFolders(): Unit = {
    val fileSeq = Seq(new File(sparkBenchTestFolder), new File(sparkBenchDemoFolder))
    fileSeq.foreach(folder => folder.mkdirs())
  }

  def deleteFolders(): Unit = {
    Utils.deleteRecursively(new File(prefix))
  }

  def generateKMeansData(rows: Int, cols: Int, outputFile: String): Unit = {

    val data: RDD[Array[Double]] = KMeansDataGenerator.generateKMeansRDD(
      spark.sparkContext,
      rows,
      KMeansWorkload.numOfClusters,
      cols,
      KMeansWorkload.scaling,
      KMeansWorkload.numOfPartitions
    )

    val schemaString = data.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, DoubleType, nullable = false))
    val schema = StructType(fields)

    val rowRDD = data.map(arr => Row(arr:_*))

    val df = spark.createDataFrame(rowRDD, schema)

    writeToDisk(outputFile, SaveModes.overwrite, df, spark)
  }
}
