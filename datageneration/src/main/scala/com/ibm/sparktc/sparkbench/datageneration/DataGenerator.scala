package com.ibm.sparktc.sparkbench.datageneration

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk

abstract class DataGenerator(conf: DataGenerationConf, spark: SparkSession) {

  def generateData(spark: SparkSession): DataFrame

  def run(): Unit = {
    val data = generateData(spark)
    writeToDisk(conf.outputDir, data, spark, conf.outputFormat)
  }

}
