package com.ibm.sparktc.sparkbench.datageneration

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk

abstract class DataGenerator(conf: DataGenerationConf, spark: SparkSession) {

//  def createSparkContext(): SparkSession = {
//    SparkSession
//      .builder()
//      .master("local[2]") //TODO this is a temp bandange, make this configurable along with all the other Spark stuff that needs to be configurable
//      .appName("spark-bench generate-data")
//      .getOrCreate()
//  }

  def generateData(spark: SparkSession): DataFrame

  def run(): Unit = {
    val data = generateData(spark)
    writeToDisk(conf.outputDir, data, conf.outputFormat, spark = spark)
  }

}
