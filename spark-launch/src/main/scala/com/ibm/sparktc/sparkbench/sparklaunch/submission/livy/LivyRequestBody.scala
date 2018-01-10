/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

package com.ibm.sparktc.sparkbench.sparklaunch.submission.livy

import com.ibm.sparktc.sparkbench.sparklaunch.confparse.SparkJobConf

case class LivyRequestBody(
                        file: String,
                        className: String,
                        args: Array[String],
                        jars: Option[List[String]] = None,
                        driverMemory: Option[String] = None,
                        driverCores: Option[Int] = None,
                        executorMemory: Option[String] = None,
                        executorCores: Option[Int] = None,
                        numExecutors: Option[Int] = None,
                        archives: Option[List[String]] = None,
                        queue: Option[String] = None,
                        name: Option[String] = None,
                        conf: Option[Map[String, String]] = None
                      )


object LivyRequestBody {

  def apply(conf: SparkJobConf): LivyRequestBody = {
    LivyRequestBody (
      file = filePathWithProperPrefix(conf.sparkBenchJar),
      className = conf.className,
      args = Array(conf.childArgs.head.replace("\"", "\\\"")),
      jars = conf.sparkArgs.get("jars") flatMap { str => Some(List(filePathWithProperPrefix(str))) },
      driverMemory = conf.sparkArgs.get("driver-memory"),
      driverCores = conf.sparkArgs.get("driver-cores") flatMap { str => Some(str.toInt)},
      executorMemory = conf.sparkArgs.get("executor-memory"),
      executorCores = conf.sparkArgs.get("executor-cores") flatMap { str => Some(str.toInt)},
      numExecutors = conf.sparkArgs.get("num-executors") flatMap { str => Some(str.toInt)},
      archives = conf.sparkArgs.get("archives") flatMap { str => Some(List(str))},
      queue = conf.sparkArgs.get("queue"),
      name = conf.sparkArgs.get("name"),
      conf = if(conf.sparkConfs.nonEmpty) Some(conf.sparkConfs) else None
    )
  }

  def filePathWithProperPrefix(str: String): String = {
    str match {
      case s if str.startsWith("/") => "file://" + str // assume local file if it starts with /
      case _ => str
    }
  }
}
