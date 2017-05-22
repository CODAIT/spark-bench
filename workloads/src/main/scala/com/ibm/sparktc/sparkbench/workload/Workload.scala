package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.load
import org.apache.spark.sql.functions.lit

trait Workload {
  val name: String
  val inputDir: Option[String]
  val workloadResultsOutputDir: Option[String]

  /**
    *  Validate that the data set has a correct schema and fix if necessary.
    *  This is to solve issues such as the KMeans load-from-disk pathway returning
    *  a DataFrame with all the rows as StringType instead of DoubleType.
   */
  def reconcileSchema(dataFrame: DataFrame): DataFrame = dataFrame

  def doWorkload(df: Option[DataFrame], sparkSession: SparkSession): DataFrame

  def run(spark: SparkSession): DataFrame = {

    val df = inputDir match {
      case None => None
      case Some(input) => {
        val rawDF = load(spark, inputDir.get)
        Some(reconcileSchema(rawDF))
      }
    }

    val res = doWorkload(df, spark)
    res.coalesce(1)
    addConfToResults(res, toMap)
  }

  def addConfToResults(df: DataFrame, m: Map[String, Any]): DataFrame = {
    def dealWithNones(a: Any): Any = a match {
      case None => ""
      case Some(b) => b
      case _ => a
    }

    var ddf: DataFrame = df
    m.foreach( keyValue => ddf = ddf.withColumn(keyValue._1, lit(dealWithNones(keyValue._2))) )
    ddf
  }

  def toMap: Map[String, Any] =
    (Map[String, Any]() /: this.getClass.getDeclaredFields) { (a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(this))
    }

}
