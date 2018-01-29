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

import com.typesafe.config._
import java.io.File

import com.ibm.sparktc.sparkbench.sparklaunch.confparse.{ConfigWrangler, SparkJobConf}
import com.ibm.sparktc.sparkbench.sparklaunch.submission.livy.LivySubmit
import com.ibm.sparktc.sparkbench.sparklaunch.submission.sparksubmit.SparkSubmit

import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.JavaConverters._
import scala.util.Try

object SparkLaunch extends App {

  override def main(args: Array[String]): Unit = {
    assert(args.nonEmpty)
    val path = args.head
    val (confSeq: Seq[SparkJobConf], parallel: Boolean) = mkConfs(new File(path))

    launchJobs(confSeq, parallel)
  }

  def mkConfs(file: File): (Seq[SparkJobConf], Boolean) = {
    val config: Config = ConfigFactory.parseFile(file)
    val sparkBenchConfig = config.getObject("spark-bench").toConfig
    val confs: Seq[SparkJobConf] = ConfigWrangler(file)
    val parallel = Try(sparkBenchConfig.getBoolean("spark-submit-parallel")).getOrElse(false)
    (confs, parallel)
  }

  private def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList(name).asScala
    workloadObjs.map(_.toConfig).toList
  }

  def launchJobs(confSeq: Seq[SparkJobConf], parallel: Boolean): Unit = {

    def launch(conf: SparkJobConf): Unit = conf.submissionParams match {
      case s if ConfigWrangler.isLivySubmit(s) => LivySubmit().launch(conf)
      case s if ConfigWrangler.isSparkSubmit(s) => SparkSubmit.launch(conf)
    }

    if (parallel) {
      val confSeqPar = confSeq.par
      confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(confSeqPar.size))
      confSeqPar.foreach(launch)
    } else confSeq.foreach(launch)
  }
}
