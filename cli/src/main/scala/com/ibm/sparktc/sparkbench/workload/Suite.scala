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

package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.utils.TypesafeAccessories.splitGroupedConfigToIndividualConfigs

case class Suite(
                  description: Option[String],
                  repeat: Int = 1,
                  parallel: Boolean = false,
                  benchmarkOutput: Option[String],
                  saveMode: String,
                  workloadConfigs: Seq[Map[String, Any]]
                )

object Suite {
  def build(confsFromArgs: Seq[Map[String, Seq[Any]]],
            description: Option[String],
            repeat: Int,
            parallel: Boolean,
            saveMode: String,
            benchmarkOutput: Option[String]): Suite = {
    Suite(
      description,
      repeat,
      parallel,
      benchmarkOutput,
      saveMode,
      confsFromArgs.flatMap( splitGroupedConfigToIndividualConfigs )
    )
  }
}
