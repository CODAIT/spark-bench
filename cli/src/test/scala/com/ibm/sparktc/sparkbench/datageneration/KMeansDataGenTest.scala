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
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

import scala.io.Source

class KMeansDataGenTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val cool = new BuildAndTeardownData("kmeans-data-gen")

  val fileName = s"${cool.sparkBenchTestFolder}/${java.util.UUID.randomUUID.toString}.csv"

  var file: File = _

  override def beforeEach() {
    cool.createFolders()
    file = new File(fileName)
  }

  override def afterEach() {
    cool.deleteFolders()
  }

  "KMeansDataGeneration" should "generate data correctly" in {

    val m = Map(
      "name" -> "kmeans",
      "rows" -> 10,
      "cols" -> 10,
      "output" -> fileName
    )

    val generator = KMeansDataGen(m)


    generator.doWorkload(spark = SparkSessionProvider.spark)


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
}