package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.{writeToDisk, load}

abstract class Workload(conf: WorkloadConfig, sparkSessOpt: Option[SparkSession]) {

  def createSparkContext(): SparkSession = {
    SparkSession
      .builder()
      .appName("spark-bench workload")
      .getOrCreate()
  }

  def doWorkload(df: DataFrame, sparkSession: SparkSession): DataFrame

  def run(): Unit = {
    val spark = sparkSessOpt match {
      case Some(ss: SparkSession) => ss
      case _ => createSparkContext()
    }
    val df = load(spark, conf.inputFormat, conf.inputDir)
    val res = doWorkload(df, spark)
    writeToDisk(conf.outputFormat, conf.outputDir, res)
  }


}
