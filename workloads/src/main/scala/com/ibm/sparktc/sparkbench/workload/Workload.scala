package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.{load}

abstract class Workload[A <: WorkloadConfig](conf: A, spark: SparkSession) {

  /**
    *  Validate that the data set has a correct schema and fix if necessary.
    *  This is to solve issues such as the KMeans load-from-disk pathway returning
    *  a DataFrame with all the rows as StringType instead of DoubleType.
   */
  def reconcileSchema(dataFrame: DataFrame): DataFrame = dataFrame

  def doWorkload(df: Option[DataFrame], sparkSession: SparkSession): DataFrame

  def run(): DataFrame = {

    val df = conf.inputDir match {
      case None => None
      case Some(input) => {
        val rawDF = load(spark, conf.inputDir.get)
        Some(reconcileSchema(rawDF))
      }
    }

    val res = doWorkload(df, spark)
    res.coalesce(1)
  }

}
