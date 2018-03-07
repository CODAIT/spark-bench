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

import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.io.Source

class OutputTest extends FlatSpec with Matchers with BeforeAndAfterAll with Capturing {
  val dataStuff = new BuildAndTeardownData("output-test")

  override def beforeAll(): Unit = {
    super.beforeAll()
    dataStuff.createFolders()
    dataStuff.generateKMeansData(1000, 5, dataStuff.kmeansFile) // scalastyle:ignore
  }

  override def afterAll(): Unit = {
    dataStuff.deleteFolders()
    super.afterAll()
  }

  "Specifying Console output" should "work" in {
    val relativePath = "/etc/testConfFile3.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    val text = Source.fromFile(path).mkString

    val (out, _) = captureOutput(CLIKickoff.main(Array(text)))
    out should not be empty
    out.split("\n").length shouldBe 9
  }

  "Want to see configuration added to results when there's crazy stuff" should "work" in {
    val relativePath = "/etc/testConfFile4.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    val text = Source.fromFile(path).mkString

    val (out, _) = captureOutput(CLIKickoff.main(Array(text)))
    out shouldBe empty
  }
}
