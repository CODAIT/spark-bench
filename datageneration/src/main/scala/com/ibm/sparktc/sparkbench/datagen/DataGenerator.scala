package com.ibm.sparktc.sparkbench.datagen

import org.apache.spark.sql.{DataFrame, SparkSession}

abstract class DataGenerator(conf: DataGenerationConf) {

  def createSparkContext(): SparkSession = {
    SparkSession
      .builder()
      .master("local[2]") //TODO this is a temp bandange, make this configurable along with all the other Spark stuff that needs to be configurable
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
