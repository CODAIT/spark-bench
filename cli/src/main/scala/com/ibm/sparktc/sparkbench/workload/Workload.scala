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

import com.ibm.sparktc.sparkbench.utils.{SaveModes, SparkBenchException}
import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs._

trait WorkloadDefaults {
  val name: String
  def apply(m: Map[String, Any]): Workload
}

trait Workload {
  val input: Option[String]
  val output: Option[String]
  val saveMode: String

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

    verifyOutput(output, saveMode, spark)
    if(saveMode == SaveModes.append){
      throw SparkBenchException("Save-mode \"append\" not available for workload results. " +
        "Please use \"errorifexists\", \"ignore\", or \"overwrite\" instead.")
    }

    val df = input.map { in =>
      val rawDF = load(spark, in)
      reconcileSchema(rawDF)
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
