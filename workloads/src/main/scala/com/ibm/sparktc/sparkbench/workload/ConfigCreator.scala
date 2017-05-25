package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.workload.exercise.{CacheTest, PartitionAndSleepWorkload, Sleep, SparkPi}
import com.ibm.sparktc.sparkbench.workload.ml.KMeansWorkload
import com.ibm.sparktc.sparkbench.workload.sql.SQLWorkload
import org.apache.spark.sql.SparkSession

object ConfigCreator {

  def apply(seq: Seq[Map[String, Any]], spark: SparkSession) = {

  }

  def mapToConf(m: Map[String, Any]): Workload = {
    val name = getOrThrow(m, "name").asInstanceOf[String].toLowerCase
    name match {
      case "timedsleep" => new PartitionAndSleepWorkload(m)
      case "kmeans" => new KMeansWorkload(m)
      case "cachetest" => new CacheTest(m)
      case "sql" => new SQLWorkload(m)
      case "sleep" => new Sleep(m)
      case "sparkpi" => new SparkPi(m)
      case _ => throw SparkBenchException(s"Unrecognized or implemented workload name: $name")
    }
  }

}
