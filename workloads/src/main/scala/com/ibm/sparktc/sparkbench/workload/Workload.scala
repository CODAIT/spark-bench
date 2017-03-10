package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.{DataFrame, SparkSession}

abstract class Workload(conf: WorkloadConfig) {

  def createSparkContext(): SparkSession = {
    SparkSession
      .builder()
      .appName("spark-bench workload")
      .getOrCreate()
  }

  def load(spark: SparkSession): DataFrame = {
    conf.inputFormat match {
      case "parquet" => spark.read.parquet(conf.inputDir)
      case "csv" | _ => spark.read.csv(conf.inputDir) //if unspecified, assume csv
    }
  }

  def doWorkload(df: DataFrame, sparkSession: SparkSession): DataFrame

  def writeToDisk(data: DataFrame): Unit = {
    conf.outputFormat match {
      case "parquet" => data.write.parquet(conf.outputDir)
      case "csv" => data.write.csv(conf.outputDir)
      case _ => new Exception("unrecognized save format")
    }
  }

  def run(): Unit = {
    val spark = createSparkContext()
    val df = load(spark)
    val res = doWorkload(df, spark)
    writeToDisk(res)
  }
}
