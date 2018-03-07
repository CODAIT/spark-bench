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

package com.ibm.sparktc.sparkbench.datageneration

import java.io.File

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.LinearRegressionDataGen
import com.ibm.sparktc.sparkbench.testfixtures.{BuildAndTeardownData, SparkSessionProvider}
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.load
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class LinearRegDataGenTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val cool = new BuildAndTeardownData("linear-reg-datagen")

  val fileName = s"${cool.sparkBenchTestFolder}/${java.util.UUID.randomUUID.toString}.parquet"
  val csvFileName = s"${cool.sparkBenchTestFolder}/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  override def beforeEach() {
    file = new File(fileName)
    cool.createFolders()
  }

  override def afterEach() {
    cool.deleteFolders()
  }

  "LinearRegressionDataGen" should "generate data correctly for Parquet output" in {

    val m = Map(
      "name" -> "kmeans",
      "rows" -> 10,
      "cols" -> 10,
      "output" -> fileName
    )

    val generator = LinearRegressionDataGen(m)

    generator.doWorkload(spark = SparkSessionProvider.spark)

//    val fileList = file.listFiles().toList.filter(_.getName.startsWith("part"))
    val fileContents = load(SparkSessionProvider.spark, fileName, Some("parquet"))
    val length: Long = fileContents.count()

    length shouldBe generator.numRows
  }

  //TODO ultimately fix LabeledPoints being output to CSV. Surely there's a way...
  it should "throw an exception when somebody tries to output to CSV" in {
    a[SparkBenchException] should be thrownBy {
      val m = Map(
        "name" -> "kmeans",
        "rows" -> 10,
        "cols" -> 10,
        "output" -> csvFileName
      )
      val generator = LinearRegressionDataGen(m)
      generator.doWorkload(spark = SparkSessionProvider.spark)
    }
  }

}