package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.SparkSession

trait WorkloadConfig {

  val name: String
  val inputDir: Option[String]
  val workloadResultsOutputDir: Option[String]

  def toMap(cc: AnyRef) =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {(a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(cc))
    }
//  /**
//    * Throws an exception if a required param is not present
//    * Fills in defaults for missing optional params
//    * Prints configuration for --verbose and --dryRun
//    * Returns an extension of WorkloadConfig
//    * @param map
//    * @return A subtype of WorkloadConfig with appropriate arguments filled in
//    */
//  def this(map: Map[String, Any], spark: SparkSession)

}
