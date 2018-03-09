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

package com.ibm.sparktc.sparkbench.sparklaunch.submission.sparksubmit

import com.ibm.sparktc.sparkbench.sparklaunch.confparse.SparkJobConf
import com.ibm.sparktc.sparkbench.sparklaunch.submission.Submitter
import com.ibm.sparktc.sparkbench.utils.SparkBenchException

import scala.sys.process.Process

object SparkSubmit extends Submitter {
  private val log = org.slf4j.LoggerFactory.getLogger(getClass)

  override def launch(conf: SparkJobConf): Unit = {
    val errorMessage = "Spark installation home not specified. Failed to find the spark-submit executable. " +
      "Please check sparkHome in your config file or $SPARK_HOME in your environment."
    val sparkHome: String = conf.submissionParams.getOrElse("spark-home", throw SparkBenchException(errorMessage)).asInstanceOf[String]
    val preppedStatement = convert(conf, sparkHome)
    submit(preppedStatement, sparkHome)
  }

  private def convert(conf: SparkJobConf, sparkHome: String): Seq[String] = {

    Seq(s"$sparkHome/bin/spark-submit") ++
      Seq("--class", conf.className) ++
      convertSparkArgs(conf.sparkArgs) ++
      convertSparkConf(conf.sparkConfs) ++
      Seq(conf.sparkBenchJar) ++
      conf.childArgs
  }

  private def submit(strSeq: Seq[String], sparkHome: String): Unit = {
    val process = Process(strSeq, None, "SPARK_HOME" -> sparkHome)
    log.info(" *** SPARK-SUBMIT: " + process.toString)
    if (process.! != 0) {
      throw new Exception(s"spark-submit failed to complete properly given these arguments: \n\t${strSeq.mkString("\n")}")
    }
  }

  private def convertSparkArgs(map: Map[String, String]): Seq[String] =
    map.foldLeft(Seq.empty[String]) {
      case (arr, (k, v)) => arr ++ Seq("--" + k, v)
    }

  private def convertSparkConf(map: Map[String, String]): Seq[String] =
    map.foldLeft(Seq.empty[String]) {
      case (arr, (k, v)) => arr ++ Seq("--conf", s"$k=$v")
    }
}
