package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.{KMeansDataGen, LinearRegressionDataGen}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.workload.exercise._
import com.ibm.sparktc.sparkbench.workload.ml.{KMeansWorkload, LogisticRegressionWorkload}
import com.ibm.sparktc.sparkbench.workload.sql.SQLWorkload

object ConfigCreator {

  private val workloads: Map[String, WorkloadDefaults] = Set(
    PartitionAndSleepWorkload,
    KMeansWorkload,
    LogisticRegressionWorkload,
    CacheTest,
    SQLWorkload,
    Sleep,
    SparkPi,
    KMeansDataGen,
    LinearRegressionDataGen
  ).map(wk => wk.name -> wk).toMap

  private def loadCustom(name: String): WorkloadDefaults = {
    import scala.reflect.runtime.universe.runtimeMirror
    val mirror = runtimeMirror(scala.reflect.runtime.universe.getClass.getClassLoader)
    val module = mirror.staticModule(name)
    mirror.reflectModule(module).instance.asInstanceOf[WorkloadDefaults]
  }

  def mapToConf(m: Map[String, Any]): Workload = {
    val name = getOrThrow(m, "name").asInstanceOf[String].toLowerCase
    val (displayName, conf) =
      if (name == "custom") {
        val className = getOrThrow(m, "class").asInstanceOf[String]
        (className, Some(loadCustom(className)))
      }
      else (name, workloads.get(name))
    conf match {
      case Some(wk) => wk.apply(m)
      case _ => throw SparkBenchException(s"Could not find workload $displayName")
    }
  }
}
