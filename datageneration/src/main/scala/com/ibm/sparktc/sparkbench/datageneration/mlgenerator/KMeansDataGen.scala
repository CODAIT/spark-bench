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

import com.ibm.sparktc.sparkbench.datageneration.{DataGenerationConf, DataGenerator}
import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrDefault

import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types._

class KMeansDataGen(conf: DataGenerationConf, spark: SparkSession) extends DataGenerator(conf, spark) {

  import KMeansDefaults._

  val m = conf.generatorSpecific //convenience

  val numCluster: Int = getOrDefault[Int](m, "clusters", NUM_OF_CLUSTERS)
  val numDim: Int = conf.numCols
  val scaling: Double = getOrDefault[Double](m, "scaling", SCALING)
  val numPar: Int = getOrDefault[Int](m, "partitions", NUM_OF_PARTITIONS)

  override def generateData(spark: SparkSession): DataFrame = {

    val data: RDD[Array[Double]] = KMeansDataGenerator.generateKMeansRDD(
      spark.sparkContext,
      conf.numRows,
      numCluster,
      numDim,
      scaling,
      numPar
    )

    val schemaString = data.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, DoubleType, nullable = false))
    val schema = StructType(fields)

    val rowRDD = data.map(arr => Row(arr:_*))

    spark.createDataFrame(rowRDD, schema)
  }
}
