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

package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.testfixtures.SparkSessionProvider
import org.scalatest.{FlatSpec, Matchers}

class PartitionAndSleepWorkloadTest extends FlatSpec with Matchers {
  val spark = SparkSessionProvider.spark

  "PartitionAndSleepWorkload" should "instantiate and run" in {
    val workload = PartitionAndSleepWorkload(Map("name" -> "timedsleep", "partitions" -> 10, "sleepms" -> 10))
    val res = workload.doWorkload(None, spark).collect
    res.length shouldBe 1
    val row  = res(0)
    row.length shouldBe 3
    row.getAs[String]("name") shouldBe "timedsleep"
    row.getAs[Long]("timestamp") shouldBe System.currentTimeMillis +- 10000
  }
}
