/*
 * (C) Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.sparktc.sparkbench.datageneration.mlgenerator

import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.{getOrDefault, _}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import com.ibm.sparktc.sparkbench.workload.Workload
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types._

case class KMeansDataGen(
                          name: String,
                          numRows: Int,
                          numCols: Int,
                          input: Option[String] = None,
                          output: Option[String],
                          k: Int,
                          scaling: Double,
                          numPartitions: Int
                        ) extends Workload {

  def this(m: Map[String, Any]) = this(
    name = getOrThrow(m, "name").asInstanceOf[String],
    numRows = getOrThrow(m, "rows").asInstanceOf[Int],
    numCols = getOrThrow(m, "cols").asInstanceOf[Int],
    output = Some(getOrThrow(m, "output").asInstanceOf[String]),
    k = getOrDefault[Int](m, "k", KMeansDefaults.K),
    scaling = getOrDefault[Double](m, "scaling", KMeansDefaults.SCALING),
    numPartitions = getOrDefault[Int](m, "partitions", KMeansDefaults.NUM_OF_PARTITIONS)
  )

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val timestamp = System.currentTimeMillis()

    val (generateTime, data): (Long, RDD[Array[Double]]) = time {
      KMeansDataGenerator.generateKMeansRDD(
        spark.sparkContext,
        numRows,
        k,
        numCols,
        scaling,
        numPartitions
      )
    }

    val (convertTime, dataDF) = time {
      val schemaString = data.first().indices.map(_.toString).mkString(" ")
      val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, DoubleType, nullable = false))
      val schema = StructType(fields)
      val rowRDD = data.map(arr => Row(arr:_*))
      spark.createDataFrame(rowRDD, schema)
    }

    val (saveTime, _) = time { writeToDisk(output.get, dataDF, spark) }

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
