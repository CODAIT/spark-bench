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
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class NotebookSimTest extends FlatSpec with Matchers with BeforeAndAfterEach with Capturing {
  val dataMaker = new BuildAndTeardownData("notebook-sim-test")

  val giantData = s"${dataMaker.sparkBenchTestFolder}/giant-kmeans-data.parquet"
  val tinyData = s"${dataMaker.sparkBenchTestFolder}/tiny-kmeans-data.parquet"

  override def beforeEach(): Unit = {
    super.beforeEach()
    dataMaker.deleteFolders()
    dataMaker.createFolders()
    dataMaker.generateKMeansData(400000, 50, giantData)
    dataMaker.generateKMeansData(100, 5, tinyData)
  }

  override def afterEach(): Unit = {
    dataMaker.deleteFolders()
  }

  "Simulating two notebook users" should "work" in {
    val relativePath = "/etc/notebook-sim.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    CLIKickoff.main(Array(path))
  }



}
