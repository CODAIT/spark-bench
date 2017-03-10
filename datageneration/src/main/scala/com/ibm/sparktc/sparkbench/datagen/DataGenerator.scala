package com.ibm.sparktc.sparkbench.datagen

import org.apache.spark.sql.{DataFrame, SparkSession}

abstract class DataGenerator(conf: DataGenerationConf) {

  def createSparkContext(): SparkSession = {
    SparkSession
      .builder()
      .appName("spark-bench generate-data")
      .getOrCreate()
  }

  def generateData(spark: SparkSession): DataFrame

  def saveAsCSV(data: DataFrame): Unit = {
  }

  def writeToDisk(data: DataFrame): Unit = {
    conf.outputFormat match {
      case "csv" => data.write.csv(conf.outputDir)
      case _ => new Exception("unrecognized save format")
    }
  }

  def run(): Unit = {
    val spark = createSparkContext()
    val data = generateData(spark)
    writeToDisk(data)
  }
}
