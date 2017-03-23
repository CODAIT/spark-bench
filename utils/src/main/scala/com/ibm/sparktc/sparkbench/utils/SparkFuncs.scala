package com.ibm.sparktc.sparkbench.utils

import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkFuncs {

  def writeToDisk(format: String, outputDir: String, data: DataFrame): Unit = {
    format match {
      case "parquet" => data.write.parquet(outputDir)
      case "csv" => data.write.csv(outputDir)
      case _ => new Exception("unrecognized save format")
    }
  }

  def load(spark: SparkSession, inputFormat: String, inputDir: String): DataFrame = {
    inputFormat match {
      case "parquet" => spark.read.parquet(inputDir)
      case "csv" | _ => spark.read.option("inferSchema", "true").csv(inputDir) //if unspecified, assume csv
    }
  }

}
