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

package com.ibm.sparktc.sparkbench.utils

import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkFuncs {

  def writeToDisk(outputDir: String, data: DataFrame, spark: SparkSession, fileFormat: Option[String] = None): Unit = {

    verifyPathNotExistsOrThrow(outputDir, s"Error: $outputDir already exists!", spark)

    val format = (outputDir, fileFormat) match {
      case ("console", None) => "console"
      case (_, None)         => outputDir.split('.').last
      case (_, Some(s))      => s
    }

    format match {
      case "parquet" => data.write.parquet(outputDir)
      case "csv" => data.write.option("header", "true").csv(outputDir)
      case "console" => data.show()
      case _ => throw new Exception(s"Unrecognized or unspecified save format. " +
        s"Please check the file extension or add a file format to your arguments: $outputDir")
    }
  }

  def load(spark: SparkSession, inputDir: String, fileFormat: Option[String] = None): DataFrame = {

    verifyPathExistsOrThrow(inputDir, s"Error: $inputDir does not exist!", spark)

    val inputFormat = fileFormat match {
      case None => inputDir.split('.').last
      case Some(s) => s
    }

    inputFormat match {
      case "parquet" => spark.read.parquet(inputDir)
      case "csv" | _ => spark.read.option("inferSchema", "true").option("header", "true").csv(inputDir) //if unspecified, assume csv
    }
  }

  def verifyPathExistsOrThrow(path: String, errorMessage: String, spark: SparkSession): String = {
    val conf = spark.sparkContext.hadoopConfiguration
    val fs = org.apache.hadoop.fs.FileSystem.get(conf)
    if (fs.exists(new org.apache.hadoop.fs.Path(path))) { path } // "It is true that this file exists"
    else { throw SparkBenchException(errorMessage) }
  }

  def verifyPathNotExistsOrThrow(path: String, errorMessage: String, spark: SparkSession): String = {
    val conf = spark.sparkContext.hadoopConfiguration
    val fs = org.apache.hadoop.fs.FileSystem.get(conf)
    if ( ! fs.exists(new org.apache.hadoop.fs.Path(path))) { path } // "It is true that this file does not exist"
    else { throw SparkBenchException(errorMessage) }
  }

  def addConfToResults(df: DataFrame, m: Map[String, Any]): DataFrame = {
    def dealWithNones(a: Any): Any = a match {
      case None => ""
      case Some(b) => b
      case _ => a
    }

    var ddf: DataFrame = df
    m.foreach( keyValue => ddf = ddf.withColumn(keyValue._1, lit(dealWithNones(keyValue._2))) )
    ddf
  }

}
