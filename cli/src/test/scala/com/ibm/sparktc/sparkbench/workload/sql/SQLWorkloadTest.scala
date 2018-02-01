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

package com.ibm.sparktc.sparkbench.workload.sql

import com.ibm.sparktc.sparkbench.testfixtures.SparkSessionProvider
import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
import com.ibm.sparktc.sparkbench.utils.SaveModes
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class SQLWorkloadTest extends FlatSpec with Matchers with BeforeAndAfterAll {
  val ioStuff = new BuildAndTeardownData("sql-workload")
  
  val spark = SparkSessionProvider.spark
  val outputFileRootName = ioStuff.sparkBenchTestFolder
  val smallData = s"$outputFileRootName/small-kmeans-data.parquet"
  val resOutput = "console"

  override def beforeAll(): Unit = {
    super.beforeAll()
    ioStuff.createFolders()
    ioStuff.generateKMeansData(1000, 10, smallData)
  }

  override def afterAll(): Unit = {
    ioStuff.deleteFolders()
    super.afterAll()
  }

  "Sql Queries over generated kmeans data" should "work" in {

    val workload = SQLWorkload(input = Some(smallData),
      output = Some(resOutput),
      saveMode = SaveModes.error,
      queryStr = "select `0` from input where `0` < -0.9",
      cache = false)

    workload.doWorkload(None, spark)
  }
}
