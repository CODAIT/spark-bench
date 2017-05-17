package com.ibm.sparktc.sparkbench.testfixtures

import java.io.File

import com.holdenkarau.spark.testing.Utils
import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

object BuildAndTeardownData {
  val inputFolder = "/tmp/spark-bench-test"
  val inputFile = s"$inputFolder/kmeans-data.parquet"
  val inputFileForExamples = "/tmp/spark-bench-demo"

  val spark = SparkSessionProvider.spark

  def deleteFiles(): Unit = {
    Utils.deleteRecursively(new File(inputFolder))
    Utils.deleteRecursively(new File(inputFileForExamples))
  }

  def deleteFiles(fileSeq: Seq[String]): Unit = {
    fileSeq.foreach(str => Utils.deleteRecursively(new File(str)))
  }

  def generateKMeansData(rows: Int, cols: Int, outputFile: String): Unit = {

    val data: RDD[Array[Double]] = KMeansDataGenerator.generateKMeansRDD(
      spark.sparkContext,
      rows,
      KMeansDefaults.NUM_OF_CLUSTERS,
      cols,
      KMeansDefaults.SCALING,
      KMeansDefaults.NUM_OF_PARTITIONS
    )

    val schemaString = data.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, DoubleType, nullable = false))
    val schema = StructType(fields)

    val rowRDD = data.map(arr => Row(arr:_*))

    val df = spark.createDataFrame(rowRDD, schema)

    writeToDisk(outputFile, df, spark)
  }

}
