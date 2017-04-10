package com.ibm.sparktc.sparkbench.utils

import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkFuncs {

  // TODO what if output file is already there or if input file doesn't exist

  def writeToDisk(outputDir: String, data: DataFrame, fileFormat: Option[String] = None): Unit = {

    val format = fileFormat match {
      case None => outputDir.split('.').last
      case Some(s) => s
    }

    format match {
      case "parquet" => data.write.parquet(outputDir)
      case "csv" => data.write.option("header", "true").csv(outputDir)
      case _ => throw new Exception(s"Unrecognized or unspecified save format. " +
        s"Please check the file extension or add a file format to your arguments: $outputDir")
    }
  }

  def load(spark: SparkSession, inputDir: String, fileFormat: Option[String] = None): DataFrame = {

    val inputFormat = fileFormat match {
      case None => inputDir.split('.').last
      case Some(s) => s
    }

    inputFormat match {
      case "parquet" => spark.read.parquet(inputDir)
      case "csv" | _ => spark.read.option("inferSchema", "true").option("header", "true").csv(inputDir) //if unspecified, assume csv
    }
  }

}
