/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
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

import com.ibm.sparktc.sparkbench.testfixtures.{BuildAndTeardownData, SparkSessionProvider}
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import org.apache.spark.graphx.GraphLoader
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class GraphDataGenTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val cool = new BuildAndTeardownData("graph-data-gen")

  val fileName = s"${cool.sparkBenchTestFolder}/${java.util.UUID.randomUUID.toString}.txt"

  var file: File = _

  override def beforeEach() {
    cool.createFolders()
    file = new File(fileName)
  }

  override def afterEach() {
    cool.deleteFolders()
  }

  "GraphDataGeneration" should "generate data correctly with all default options" in {

    val m = Map(
      "name" -> "graph-data-generator",
      "vertices" -> 100,
      "output" -> fileName
    )
    val generator = GraphDataGen(m)
    generator.doWorkload(spark = SparkSessionProvider.spark)
    val res = GraphLoader.edgeListFile(SparkSessionProvider.spark.sparkContext, fileName)

    res.vertices.count() shouldBe m("vertices")
  }

  it should "throw an error for any output format but .txt" in {
    val m1 = Map(
      "name" -> "graph-data-generator",
      "vertices" -> 100,
      "output" -> "/my-cool-file.csv"
    )
    val m2 = Map(
      "name" -> "graph-data-generator",
      "vertices" -> 100,
      "output" -> "/my-cool-file.parquet"
    )
    val m3 = Map(
      "name" -> "graph-data-generator",
      "vertices" -> 100,
      "output" -> "/my-cool-file.tsv"
    )

    a [SparkBenchException] should be thrownBy GraphDataGen(m1)
    a [SparkBenchException] should be thrownBy GraphDataGen(m2)
    a [SparkBenchException] should be thrownBy GraphDataGen(m3)
  }

  it should "throw errors when required values are missing" in {
    // Missing vertices
    val m1 = Map(
      "name" -> "graph-data-generator",
      "output" -> "/my-cool-file.csv"
    )
    // Missing output file name
    val m2 = Map(
      "name" -> "graph-data-generator",
      "vertices" -> 100
    )
    a [SparkBenchException] should be thrownBy GraphDataGen(m1)
    a [SparkBenchException] should be thrownBy GraphDataGen(m2)
  }
}
