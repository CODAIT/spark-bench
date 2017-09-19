/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

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
