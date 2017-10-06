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

import java.io.File

import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.workload.MultipleSuiteKickoff

object CLIKickoff extends App {

  override def main(args: Array[String]): Unit = {
    args.length match {
      case 1 => {
//        val file = new File(args.head)
//        if(!file.exists()) throw SparkBenchException(s"Cannot find configuration file: ${file.getPath}")
        val worksuites = Configurator(args.head)
        MultipleSuiteKickoff.run(worksuites)
      }
      case _ => throw new IllegalArgumentException("Requires exactly one option: config file path")
    }
  }
}
