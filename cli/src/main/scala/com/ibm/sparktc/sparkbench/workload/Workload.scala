package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.{load, addConfToResults}

trait WorkloadDefaults {
  val name: String
  def apply(m: Map[String, Any]): Workload
}

trait Workload {
  val input: Option[String]
  val output: Option[String]

  /**
    *  Validate that the data set has a correct schema and fix if necessary.
    *  This is to solve issues such as the KMeans load-from-disk pathway returning
    *  a DataFrame with all the rows as StringType instead of DoubleType.
   */
  def reconcileSchema(dataFrame: DataFrame): DataFrame = dataFrame

  /**
    * Actually run the workload.  Takes an optional DataFrame as input if the user
    * supplies an inputDir, and returns the generated results DataFrame.
    */
  def doWorkload(df: Option[DataFrame], sparkSession: SparkSession): DataFrame

  def run(spark: SparkSession): DataFrame = {

    val df = input match {
      case None => None
      case Some(in) => {
        val rawDF = load(spark, in)
        Some(reconcileSchema(rawDF))
      }
    }

    val res = doWorkload(df, spark).coalesce(1)
    addConfToResults(res, toMap)
  }

  def toMap: Map[String, Any] =
    (Map[String, Any]() /: this.getClass.getDeclaredFields) { (a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(this))
    }
}
