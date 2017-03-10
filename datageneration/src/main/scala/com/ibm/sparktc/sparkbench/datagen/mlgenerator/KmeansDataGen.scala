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

package com.ibm.sparktc.sparkbench.datagen.mlgenerator

import com.ibm.sparktc.sparkbench.datagen.{DataGenerationConf, DataGenerator}
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types._


class KmeansDataGen(conf: DataGenerationConf) extends DataGenerator(conf) {

  val numCluster: Int = conf.generatorSpecific.getOrElse("clusters", "2").asInstanceOf[String].toInt
  val numDim: Int = conf.generatorSpecific.getOrElse("dimensions", "2").asInstanceOf[String].toInt
  val scaling = conf.generatorSpecific.getOrElse("scaling", "2").asInstanceOf[String].toInt
  val numPar = conf.generatorSpecific.getOrElse("partitions", "2").asInstanceOf[String].toInt

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
