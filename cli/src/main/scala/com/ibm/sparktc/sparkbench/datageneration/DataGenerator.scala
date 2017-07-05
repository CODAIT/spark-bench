package com.ibm.sparktc.sparkbench.datageneration

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk

trait DataGenerator {

  val generatorName: String
  val numRows: Int
  val numCols: Int
  val output: String

  def generateData(spark: SparkSession): DataFrame

  def run(spark: SparkSession): Unit = {
    val data = generateData(spark)
    writeToDisk(output, data, spark)
  }

}
