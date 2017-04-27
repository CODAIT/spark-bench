package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.load

abstract class Workload(conf: WorkloadConfig, spark: SparkSession) {

  /**
    *  Validate that the data set has a correct schema and fix if necessary.
    *  This is to solve issues such as the KMeans load-from-disk pathway returning
    *  a DataFrame with all the rows as StringType instead of DoubleType.
   */
  def reconcileSchema(dataFrame: DataFrame): DataFrame

  def doWorkload(df: DataFrame, sparkSession: SparkSession): DataFrame

  def run(): DataFrame = {



    val rawDF = load(spark, conf.inputDir)
    val df = reconcileSchema(rawDF)
    val res = doWorkload(df, spark)
    res.coalesce(1)

  }

}
