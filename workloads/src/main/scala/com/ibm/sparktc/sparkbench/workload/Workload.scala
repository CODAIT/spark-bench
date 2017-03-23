package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.{load, writeToDisk}

abstract class Workload(conf: WorkloadConfig, sparkSessOpt: Option[SparkSession]) {

  def createSparkContext(): SparkSession = {
    SparkSession
      .builder()
      .appName("spark-bench workload")
      .getOrCreate()
  }

  /**
    *  Validate that the data set has a correct schema and fix if necessary.
    *  This is to solve issues such as the KMeans load-from-disk pathway returning
    *  a DataFrame with all the rows as StringType instead of DoubleType.
   */
  def reconcileSchema(dataFrame: DataFrame): DataFrame

  def doWorkload(df: DataFrame, sparkSession: SparkSession): DataFrame

  def run(): Unit = {
    val spark = sparkSessOpt match {
      case Some(ss: SparkSession) => ss
      case _ => createSparkContext()
    }

    val rawdf = load(spark, conf.inputFormat, conf.inputDir)
    val df = reconcileSchema(rawdf)
    val res = doWorkload(df, spark)
    val coalesced = res.coalesce(1)
    writeToDisk(conf.outputFormat, conf.outputDir, coalesced)
  }


}
