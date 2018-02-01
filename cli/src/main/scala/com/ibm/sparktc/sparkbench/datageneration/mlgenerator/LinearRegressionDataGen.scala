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

package com.ibm.sparktc.sparkbench.datageneration.mlgenerator

import org.apache.spark.mllib.util.LinearDataGenerator
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import com.ibm.sparktc.sparkbench.utils.{SaveModes, SparkBenchException}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.{getOrDefault, getOrThrow, time}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}

object LinearRegressionDataGen extends WorkloadDefaults {
  val name = "data-generation-lr"
  // Application parameters #1million points have 200M data size
  val numOfExamples: Int = 40000
  val numOfFeatures: Int = 4
  val eps: Double = 0.5
  val intercepts: Double = 0.1
  val numOfPartitions: Int = 10
  val maxIteration: Int = 3
  override def apply(m: Map[String, Any]) = new LinearRegressionDataGen(
    numRows = getOrThrow(m, "rows").asInstanceOf[Int],
    numCols = getOrThrow(m, "cols").asInstanceOf[Int],
    output = Some(getOrThrow(m, "output").asInstanceOf[String]),
    saveMode = getOrDefault[String](m, "save-mode", SaveModes.error),
    eps = getOrDefault[Double](m, "eps", eps),
    intercepts = getOrDefault[Double](m, "intercepts", intercepts),
    numPartitions = getOrDefault[Int](m, "partitions", numOfPartitions)
  )
}

case class LinearRegressionDataGen (
                                      numRows: Int,
                                      numCols: Int,
                                      input: Option[String] = None,
                                      output: Option[String],
                                      saveMode: String,
                                      eps: Double,
                                      intercepts: Double,
                                      numPartitions: Int
                                   ) extends Workload {

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {

    val timestamp = System.currentTimeMillis()

    val (generateTime, data): (Long, RDD[LabeledPoint]) = time {
      LinearDataGenerator.generateLinearRDD(
        spark.sparkContext,
        numRows,
        numCols,
        eps,
        numPartitions,
        intercepts
      )
    }

    import spark.implicits._
    val (convertTime, dataDF) = time {
      data.toDF
    }

    val (saveTime, _) = time {
      val outputstr = output.get
      if(outputstr.endsWith(".csv")) throw SparkBenchException("LabeledPoints cannot be saved to CSV. Please try outputting to Parquet instead.")
      writeToDisk(output.get, saveMode, dataDF, spark)
    }//TODO you can't output this to CSV. Parquet is fine

    val timeResultSchema = StructType(
      List(
        StructField("name", StringType, nullable = false),
        StructField("timestamp", LongType, nullable = false),
        StructField("generate", LongType, nullable = true),
        StructField("convert", LongType, nullable = true),
        StructField("save", LongType, nullable = true),
        StructField("total_runtime", LongType, nullable = false)
      )
    )

    val total = generateTime + convertTime + saveTime

    val timeList = spark.sparkContext.parallelize(Seq(Row("kmeans", timestamp, generateTime, convertTime, saveTime, total)))

    spark.createDataFrame(timeList, timeResultSchema)

  }
}
