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

package com.ibm.sparktc.sparkbench.sparklaunch

import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class SparkLaunchOneSparkContextPerRunTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val dataShiznit = new BuildAndTeardownData("spark-launch-one-spark-context-per-run")

  override def beforeEach(): Unit = {
    super.beforeEach()
    dataShiznit.createFolders()
    dataShiznit.generateKMeansData(1000, 5, dataShiznit.kmeansFile)
  }

  override def afterEach(): Unit = {
    dataShiznit.deleteFolders()
  }
  "Launching Spark" should "work" in {
    val relativePath = "/etc/craig-test.conf"
    val resource = getClass.getResource(relativePath)
    val path = resource.getPath
    SparkLaunch.main(Array(path))
  }
}
