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

package com.ibm.sparktc.sparkbench.workload.sql

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.utils.SparkFuncs._
import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import org.apache.spark.sql.{DataFrame, SparkSession}

case class SQLWorkloadResult(
                            name: String,
                            timestamp: Long,
                            loadTime: Long,
                            queryTime: Long,
                            saveTime: Long = 0L,
                            total_Runtime: Long
                            )

object SQLWorkload extends WorkloadDefaults {
  val name = "sql"
  def apply(m: Map[String, Any]) =
    new SQLWorkload(input = m.get("input").map(_.asInstanceOf[String]),
      output = m.get("output").map(_.asInstanceOf[String]),
      queryStr = getOrThrow(m, "query").asInstanceOf[String],
      cache = getOrDefault[Boolean](m, "cache", false)
    )

}

case class SQLWorkload (input: Option[String],
                        output: Option[String] = None,
                        queryStr: String,
                        cache: Boolean) extends Workload {

  def loadFromDisk(spark: SparkSession): (Long, DataFrame) = time {
    val df = load(spark, input.get)
    if(cache) df.cache()
    df
  }

  def query(df: DataFrame, spark: SparkSession): (Long, DataFrame) = time {
    df.createOrReplaceTempView("input")
    spark.sqlContext.sql(queryStr)
  }

  def save(res: DataFrame, where: String, spark: SparkSession): (Long, Unit) = time {
    writeToDisk(where, res, spark)
  }

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val timestamp = System.currentTimeMillis()
    val (loadtime, df) = loadFromDisk(spark)
    val (querytime, res) = query(df, spark)
    val (savetime, _) = output match {
      case Some(dir) => save(res, dir, spark)
      case _ => (0L, Unit)
    }
    val total = loadtime + querytime + savetime

    spark.createDataFrame(Seq(
      SQLWorkloadResult(
        "sql",
        timestamp,
        loadtime,
        querytime,
        savetime,
        total
      )
    ))
  }

}

