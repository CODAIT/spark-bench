package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.workload.exercise.TimedSleepWorkloadConf
import com.ibm.sparktc.sparkbench.workload.mlworkloads.KMeansWorkloadConfig
import org.apache.spark.sql.SparkSession

object ConfigCreator {

  def apply(seq: Seq[Map[String, Any]], spark: SparkSession) = {

  }

  def mapToConf(m: Map[String, Any], spark: SparkSession) = {
    val name = getOrThrow(m, "name").asInstanceOf[String].toLowerCase
    name match {
      case "timedsleep" => new TimedSleepWorkloadConf(m, spark)
      case "kmeans" => new KMeansWorkloadConfig(m, spark)
      case _ => throw SparkBenchException(s"Unrecognized or implemented workload name: $name")
    }
  }

}
