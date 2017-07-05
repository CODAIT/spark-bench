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

import com.ibm.sparktc.sparkbench.datageneration.DataGenerator
import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.{getOrDefault, _}
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types._

case class KMeansDataGen(
                          generatorName: String,
                          numRows: Int,
                          numCols: Int,
                          output: String,
                          k: Int,
                          scaling: Double,
                          numPartitions: Int
                        ) extends DataGenerator {

  import KMeansDefaults._

  def this(m: Map[String, Any]) = this(
    generatorName = verifyOrThrow(m, "name", "kmeans", s"Required field name does not match"),
    numRows = getOrThrow(m, "rows"),
    numCols = getOrThrow(m, "cols"),
    output = getOrThrow(m, "output"),
    k = getOrDefault[Int](m, "k", K),
    scaling = getOrDefault[Double](m, "scaling", SCALING),
    numPartitions = getOrDefault[Int](m, "partitions", NUM_OF_PARTITIONS)
  )

  override def generateData(spark: SparkSession): DataFrame = {

    val data: RDD[Array[Double]] = KMeansDataGenerator.generateKMeansRDD(
      spark.sparkContext,
      numRows,
      k,
      numCols,
      scaling,
      numPartitions
    )

    val schemaString = data.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, DoubleType, nullable = false))
    val schema = StructType(fields)

    val rowRDD = data.map(arr => Row(arr:_*))

    spark.createDataFrame(rowRDD, schema)
  }
}
