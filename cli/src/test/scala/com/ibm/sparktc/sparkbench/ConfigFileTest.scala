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

package com.ibm.sparktc.sparkbench

import java.io.File

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.io.Source

class ConfigFileTest extends FlatSpec with Matchers with BeforeAndAfterAll with Capturing {

  val dataShiznit = new BuildAndTeardownData("configfiletest")

  override def beforeAll(): Unit = {
    super.beforeAll()
    dataShiznit.deleteFolders()
    dataShiznit.createFolders()
  }

  override def afterAll(): Unit = {
    dataShiznit.deleteFolders()
  }

  val kmeansData = new File("/tmp/spark-bench-scalatest/configfiletest/kmeans-data.parquet")
  val output1 = new File("/tmp/spark-bench-scalatest/configfiletest/conf-file-output-1.csv")
  val output2 = new File("/tmp/spark-bench-scalatest/configfiletest/conf-file-output-2.parquet")

  "Spark-bench run through a config file serially" should "work" in {
    kmeansData.exists() shouldBe false

    val relativePath = "/etc/testConfFile1.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))

    kmeansData.exists() shouldBe true
    output1.exists() shouldBe true
    output2.exists() shouldBe true

    val fileList = output1.listFiles().toList.filter(_.getName.startsWith("part"))

    val fileContents: List[String] =
        Source.fromFile(fileList.head)
          .getLines()
          .toList


    val length: Int = fileContents.length

    (length > 0) shouldBe true
  }

  "Spark-bench run through a config file with the suites running in parallel" should "work" in {
    kmeansData.exists() shouldBe true
    val relativePath = "/etc/testConfFile2.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))
  }
}
