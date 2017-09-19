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
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}

object PartitionAndSleepWorkload extends WorkloadDefaults {
  val name = "timedsleep"
  val partitions: Int = 48
  val sleepms: Long = 12000L

  def apply(m: Map[String, Any]) = new PartitionAndSleepWorkload(
    input = None,
    output = None,
    partitions = getOrDefault[Int](m, "partitions", partitions),
    sleepMS = getOrDefault[Long](m, "sleepms", sleepms, any2Long))
}

case class PartitionAndSleepWorkload(input: Option[String] = None,
                                     output: Option[String] = None,
                                     partitions: Int,
                                     sleepMS: Long) extends Workload {

  def doStuff(spark: SparkSession): (Long, Unit) = time {

    val ms = sleepMS
    val stuff: RDD[Int] = spark.sparkContext.parallelize(0 until partitions * 100, partitions)

    val cool: RDD[(Int, Int)] = stuff.map { i =>
      Thread.sleep(ms)
      (i % 10, i + 42)
    }

    val yeah = cool.reduceByKey(_ + _)
    yeah.collect()
  }

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val (t, _) = doStuff(spark)

    val schema = StructType(
      List(
        StructField("name", StringType, nullable = false),
        StructField("timestamp", LongType, nullable = false),
        StructField("runtime", LongType, nullable = false)
      )
    )

    val timeList = spark.sparkContext.parallelize(Seq(Row("timedsleep", System.currentTimeMillis(), t)))

    spark.createDataFrame(timeList, schema)
  }
}

