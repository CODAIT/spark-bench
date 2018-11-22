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

import org.apache.hadoop.fs.Path
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{DataFrame, SparkSession}
import com.databricks.spark.avro._

object SparkFuncs {

  private def parseFormat(outputDir: String, fileFormat: Option[String]): String = (outputDir, fileFormat) match {
    case (Formats.console, None) => Formats.console
    case (_, None)         => outputDir.split('.').last
    case (_, Some(s))      => s
  }

  def verifyOutput(outputDir: Option[String], saveMode: String, spark: SparkSession, fileFormat: Option[String] = None): Unit = {
    verifyCanWriteOrThrow(outputDir, saveMode, spark, fileFormat)
  }
  
  def verifyCanWrite(outputDir: String, saveMode: String, spark: SparkSession, fileFormat: Option[String] = None): Boolean = {
    if(outputDir == Formats.console) true
    else {
      (pathExists(outputDir, spark), saveMode) match {
        case (false, _) => true
        case (true, SaveModes.overwrite) => true
        case (true, SaveModes.append) => true
        case (true, SaveModes.error) => false
        case (true, SaveModes.ignore) => true // allow write operation to no-op when the time comes
        case (true, _) => throw SparkBenchException(s"Unrecognized or unimplemented save-mode: $saveMode")
      }
    }
  }

  def verifyFormat(outputDir: String, fileFormat: Option[String] = None): Boolean = {
    val format = parseFormat(outputDir, fileFormat)

    format match {
      case Formats.parquet => true
      case Formats.csv => true
      case Formats.orc => true
      case Formats.avro => true
      case Formats.json => true
      case Formats.console => true
      case _ => false
    }
  }

  def pathExists(path: String, spark: SparkSession): Boolean = {
    val p = new Path(path)
    p.getFileSystem(spark.sparkContext.hadoopConfiguration).exists(p)
  }

  def verifyPathExistsOrThrow(path: String, errorMessage: String, spark: SparkSession): String = {
    if (pathExists(path, spark)) { path } // "It is true that this file exists"
    else { throw SparkBenchException(errorMessage) }
  }

  def verifyPathNotExistsOrThrow(path: String, errorMessage: String, spark: SparkSession): String = {
    if (!pathExists(path, spark)) { path } // "It is true that this file does not exist"
    else { throw SparkBenchException(errorMessage) }
  }

  def verifyCanWriteOrThrow(outputDir: Option[String], saveMode: String, spark: SparkSession, fileFormat: Option[String] = None): Unit = {
    if(outputDir.nonEmpty) {
      if(!verifyCanWrite(outputDir.get, saveMode, spark, fileFormat)){
        throw SparkBenchException(s"File ${outputDir.get} already exists and save-mode $saveMode prevents further action")
      }
    }
  }

  def verifyFormatOrThrow(outputDir: Option[String], fileFormat: Option[String] = None): Unit = {
    if(outputDir.nonEmpty) {
      if(!verifyFormat(outputDir.get, fileFormat)) {
        throw new Exception(s"Unrecognized or unspecified save format. " +
          s"Please check the file extension or add a file format to your arguments: $outputDir")
      }
    }
  }

  def writeToDisk(outputDir: String, saveMode: String, data: DataFrame, spark: SparkSession, fileFormat: Option[String] = None): Unit = {

    val format = parseFormat(outputDir, fileFormat)

    format match {
      case Formats.parquet => data.write.mode(saveMode).parquet(outputDir)
      case Formats.csv => data.write.mode(saveMode).option("header", "true").csv(outputDir)
      case Formats.orc => data.write.mode(saveMode).orc(outputDir)
      case Formats.avro => data.write.mode(saveMode).avro(outputDir)
      case Formats.json => data.write.mode(saveMode).json(outputDir)
      case Formats.console => data.show()
      case _ => throw new Exception(s"Unrecognized or unspecified save format: $format. " +
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
      case Formats.parquet => spark.read.parquet(inputDir)
      case Formats.orc => spark.read.orc(inputDir)
      case Formats.avro => spark.read.avro(inputDir)
      case Formats.json => spark.read.json(inputDir)
      case Formats.csv | _ => spark.read.option("inferSchema", "true").option("header", "true").csv(inputDir) //if unspecified, assume csv
    }
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
