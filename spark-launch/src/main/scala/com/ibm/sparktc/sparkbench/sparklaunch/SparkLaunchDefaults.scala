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

object SparkLaunchDefaults {
  val topLevelConfObject = "spark-bench"
  val sparkSubmitObject = "spark-submit-config"
  val sparkSubmitParallel = "spark-submit-parallel"
  val sparkArgs = "spark-args"
  val sparkConf = "conf"
  val suites = "workload-suites"
  val suitesParallel = "suites-parallel"
  val sparkHome = "spark-home"
  val livy = "livy"
  val url = "url"
  val pollSeconds = "poll-seconds"
  val sparkBenchJar = "spark-bench-jar"
  val enableHive = "enable-hive"

  val suitesParallelDefaultValue = false
  val pollSecondsDefault = 5
}
