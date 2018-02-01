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

package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.workload.Suite
import org.scalatest.{FlatSpec, Matchers}

class HelpersTest extends FlatSpec with Matchers {
  "CLIKickoff" should "reject invalid argument strings" in {
    an [IllegalArgumentException] should be thrownBy CLIKickoff.main(Array())
    an [Exception] should be thrownBy CLIKickoff.main(Array("this is totally not valid HOCON {{}"))
  }
  "Suite" should "split workload configs properly" in {
    val conf = Seq(
      Map("a" -> Seq(1, 2, 3), "b" -> Seq(4, 5)),
      Map("z" -> Seq(10, 20, 30))
    )
    val res = Seq(
      Map("a" -> 1, "b" -> 4),
      Map("a" -> 1, "b" -> 5),
      Map("a" -> 2, "b" -> 4),
      Map("a" -> 2, "b" -> 5),
      Map("a" -> 3, "b" -> 4),
      Map("a" -> 3, "b" -> 5),
      Map("z" -> 10),
      Map("z" -> 20),
      Map("z" -> 30)
    )
    val suite = Suite.build(conf, Some("description"), 1, false, "error", Some("output"))
    suite.description shouldBe Some("description")
    suite.repeat shouldBe 1
    suite.parallel shouldBe false
    suite.benchmarkOutput shouldBe Some("output")
    suite.workloadConfigs shouldBe res
  }
}
