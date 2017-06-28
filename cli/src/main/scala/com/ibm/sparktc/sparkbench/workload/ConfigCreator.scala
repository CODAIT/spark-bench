package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.workload.exercise.{CacheTest, PartitionAndSleepWorkload, Sleep, SparkPi}
import com.ibm.sparktc.sparkbench.workload.ml.{KMeansWorkload, LogisticRegressionWorkload}
import com.ibm.sparktc.sparkbench.workload.sql.SQLWorkload
import org.apache.spark.sql.SparkSession

object ConfigCreator {

  def apply(seq: Seq[Map[String, Any]], spark: SparkSession) = {

  }

  def mapToConf(m: Map[String, Any]): Workload = {
    val name = getOrThrow(m, "name").asInstanceOf[String].toLowerCase
    name match {
      case PartitionAndSleepWorkload.name => new PartitionAndSleepWorkload(m)
      case KMeansWorkload.name => new KMeansWorkload(m)
      case LogisticRegressionWorkload.name => new LogisticRegressionWorkload(m)
      case CacheTest.name => new CacheTest(m)
      case SQLWorkload.name => new SQLWorkload(m)
      case "sleep" => new Sleep(m)
      case "sparkpi" => new SparkPi(m)
      case _ => throw SparkBenchException(s"Unrecognized or implemented workload name: $name")
    }
  }

}
