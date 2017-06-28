package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.{load, addConfToResults}
import com.ibm.sparktc.sparkbench.cli.SuiteArgs
import org.rogach.scallop._

trait WorkloadDefaults {
  val name: String
  val subcommand: SuiteArgs = new SuiteArgs(name)
  //def parseArgs(args: Map[String, Seq[Any]]): Suite = subcommand.parseWorkoadArgs()(args)
  val args: Map[String, ScallopOption[_ <: List[Any]]] = Map.empty
  def parseArgs: Suite = subcommand.parseWorkoadArgs(args.map(x => (x._1, x._2.apply())))
}

trait Workload {
  val inputDir: Option[String]
  val workloadResultsOutputDir: Option[String]

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

  def toMap: Map[String, Any] =
    (Map[String, Any]() /: this.getClass.getDeclaredFields) { (a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(this))
    }

  final def run(spark: SparkSession): DataFrame = {
    val df = inputDir.flatMap(input => Some(reconcileSchema(load(spark, input))))
    val res = doWorkload(df, spark).coalesce(1) // TODO Why do we coalesce to 1?
    addConfToResults(res, toMap)
  }
}
