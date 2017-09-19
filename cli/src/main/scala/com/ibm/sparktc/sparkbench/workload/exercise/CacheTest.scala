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

package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.sql.{DataFrame, SparkSession}

case class CacheTestResult(name: String, timestamp: Long, runTime1: Long, runTime2: Long, runTime3: Long)

object CacheTest extends WorkloadDefaults {
  val name = "cachetest"
  def apply(m: Map[String, Any]) =
    new CacheTest(input = m.get("input").map(_.asInstanceOf[String]),
      output = m.get("workloadresultsoutputdir").map(_.asInstanceOf[String]),
      sleepMs = getOrDefault[Long](m, "sleepMs", 1000L))
}

case class CacheTest(input: Option[String],
                    output: Option[String],
                    sleepMs: Long) extends Workload {

  def doWorkload(df: Option[DataFrame], spark: SparkSession): DataFrame = {
    import spark.implicits._

    val cached = df.getOrElse(Seq.empty[(Int)].toDF).cache

    val (resultTime1, _) = time(cached.count)
    Thread.sleep(sleepMs)
    val (resultTime2, _) = time(cached.count)
    Thread.sleep(sleepMs)
    val (resultTime3, _) = time(cached.count)

    val now = System.currentTimeMillis()
    spark.createDataFrame(Seq(CacheTestResult("cachetest", now, resultTime1, resultTime2, resultTime3)))
  }
}
