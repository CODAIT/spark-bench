package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.workload.exercise._
import com.ibm.sparktc.sparkbench.workload.ml.{KMeansWorkload, LogisticRegressionWorkload}
import com.ibm.sparktc.sparkbench.workload.sql.SQLWorkload

object ConfigCreator {
  private val internalWorkloads: Set[WorkloadDefaults] = Set(
    PartitionAndSleepWorkload,
    KMeansWorkload,
    LogisticRegressionWorkload,
    CacheTest,
    SQLWorkload,
    Sleep,
    SparkPi,
    HelloString
  )
  private val workloads = internalWorkloads.map(wk => wk.name -> wk).toMap
  def mapToConf(m: Map[String, Any]): Workload = {
    val name = getOrThrow(m, "name").asInstanceOf[String].toLowerCase
    workloads.get(name) match {
      case Some(wk) => wk.apply(m)
      case _ => throw SparkBenchException(s"Unrecognized or unimplemented workload: $name")
    }
  }
}
