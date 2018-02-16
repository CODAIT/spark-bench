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

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.KMeansDataGen
import com.ibm.sparktc.sparkbench.testfixtures.{BuildAndTeardownData, SparkSessionProvider}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.io.Source

class KMeansDataGenTest extends FlatSpec with Matchers with BeforeAndAfterAll {
  val cool = new BuildAndTeardownData("kmeans-data-gen")

  val fileName = s"${cool.sparkBenchTestFolder}/${java.util.UUID.randomUUID.toString}"

  var file: File = _

  override def beforeAll() {
    cool.createFolders()
  }

  override def afterAll() {
    cool.deleteFolders()
  }

  "KMeansDataGeneration" should "generate a csv correctly" in {

    val csvFile = s"$fileName.csv"

    val m = Map(
      "name" -> "kmeans",
      "rows" -> 10,
      "cols" -> 10,
      "output" -> csvFile
    )

    val generator = KMeansDataGen(m)

    generator.doWorkload(spark = SparkSessionProvider.spark)

    file = new File(csvFile)

    val fileList = file.listFiles().toList.filter(_.getName.startsWith("part"))

    val fileContents: List[String] = fileList
      .flatMap(
        Source.fromFile(_)
          .getLines()
          .toList
      )

    val length: Int = fileContents.length

    /*
    *  Okay, some explanation here. I made headers default for csv, so there's going to be
    *  one extra header line per partition file. If the csv header option ever changes, this
    *  test will break, but now you know what's going on so you can fix it :)
    */
    length shouldBe generator.numRows + fileList.length
  }

  it should "generate an ORC file correctly" in {
    val spark = SparkSessionProvider.spark

    val orcFile = s"$fileName.orc"

    val m = Map(
      "name" -> "kmeans",
      "rows" -> 10,
      "cols" -> 10,
      "output" -> orcFile
    )

    val generator = KMeansDataGen(m)

    generator.doWorkload(spark = spark)

    file = new File(orcFile)

    val list = file.listFiles().toList
    val fileList = list.filter(_.getName.startsWith("part"))

    fileList.length should be > 0

    println(s"reading file $orcFile")

    val fromDisk = spark.read.orc(orcFile)
    val rows = fromDisk.count()
    rows shouldBe 10
  }
}