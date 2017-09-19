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
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class SparkPiTest extends FlatSpec with Matchers with BeforeAndAfterEach {
  val spark = SparkSessionProvider.spark

  "SparkPi" should "instantiate and run" in {
    val workload = SparkPi(Map("name" -> "sparkpi", "slices" -> 4))
    val res = workload.doWorkload(None, spark).collect
    res.length shouldBe 1
    val row = res(0)
    row.length shouldBe 4
    row.getAs[String]("name") shouldBe "sparkpi"
    row.getAs[Double]("pi_approximate") shouldBe 3.14 +- 1
  }
}
