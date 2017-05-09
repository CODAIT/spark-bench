package com.ibm.sparktc.sparkbench.utils

import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkFuncs {

  // TODO what if output file is already there or if input file doesn't exist

  def writeToDisk(outputDir: String, data: DataFrame, fileFormat: Option[String] = None, spark: SparkSession): Unit = {

    verifyPathNotExistsOrThrow(outputDir, s"Error: $outputDir already exists!", spark)

    val format = (outputDir, fileFormat) match {
      case ("console", None) => "console"
      case (_, None)         => outputDir.split('.').last
      case (_, Some(s))      => s
    }

    format match {
      case "parquet" => data.write.parquet(outputDir)
      case "csv" => data.write.option("header", "true").csv(outputDir)
      case "console" => data.show()
      case _ => throw new Exception(s"Unrecognized or unspecified save format. " +
        s"Please check the file extension or add a file format to your arguments: $outputDir")
    }
  }

  def load(spark: SparkSession, inputDir: String, fileFormat: Option[String] = None): DataFrame = {

    verifyPathExistsOrThrow(inputDir, s"Error: $inputDir does not exist!", spark)

    val inputFormat = fileFormat match {
      case None => inputDir.split('.').last
      case Some(s) => s
    }

    inputFormat match {
      case "parquet" => spark.read.parquet(inputDir)
      case "csv" | _ => spark.read.option("inferSchema", "true").option("header", "true").csv(inputDir) //if unspecified, assume csv
    }
  }

  def verifyPathExistsOrThrow(path: String, errorMessage: String, spark: SparkSession): String = {
    val conf = spark.sparkContext.hadoopConfiguration
    val fs = org.apache.hadoop.fs.FileSystem.get(conf)
    if (fs.exists(new org.apache.hadoop.fs.Path(path))) { path } // "It is true that this file exists"
    else { throw SparkBenchException(errorMessage) }
  }

  def verifyPathNotExistsOrThrow(path: String, errorMessage: String, spark: SparkSession): String = {
    val conf = spark.sparkContext.hadoopConfiguration
    val fs = org.apache.hadoop.fs.FileSystem.get(conf)
    if ( ! fs.exists(new org.apache.hadoop.fs.Path(path))) { path } // "It is true that this file does not exist"
    else { throw SparkBenchException(errorMessage) }
  }

}
