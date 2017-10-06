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
import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.JavaConverters._
import scala.sys.process._
import scala.util.Try
import com.ibm.sparktc.sparkbench.sparklaunch.{SparkLaunchDefaults => SLD}

object SparkLaunch extends App {

  override def main(args: Array[String]): Unit = {
    assert(args.nonEmpty)
    val path = args.head
    val (confSeq: Seq[SparkSubmitScriptConf], parallel: Boolean) = mkConfs(new File(path))
    launchSparkSubmitScripts(confSeq, parallel)
//    rmTmpFiles(confSeq.map(_._2))
  }

  def mkConfs(file: File): (Seq[SparkSubmitScriptConf], Boolean) = {
    val config: Config = ConfigFactory.parseFile(file)
    val sparkBenchConfig = config.getObject("spark-bench").toConfig
    val confs: Seq[SparkSubmitScriptConf] = ConfigWrangler(file)
    val parallel = Try(sparkBenchConfig.getBoolean("spark-submit-parallel")).getOrElse(false)
    (confs, parallel)
  }


  private def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList(name).asScala
    workloadObjs.map(_.toConfig).toList
  }

  def launchSparkSubmitScripts(confSeq: Seq[SparkSubmitScriptConf], parallel: Boolean): Unit = {
    if (parallel) {
      val confSeqPar = confSeq.par
      confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(confSeqPar.size))
      confSeqPar.foreach(launch)
    } else confSeq.foreach(launch)
  }

  def launch(conf: SparkSubmitScriptConf): Unit = {
    val argz: Array[String] = conf.toSparkSubmitArgs
    val submitProc = Process(Seq(s"${conf.sparkHome}/bin/spark-submit") ++ argz, None, "SPARK_HOME" -> conf.sparkHome)
    println(" *** SPARK-SUBMIT: " + submitProc.toString)
    if (submitProc.! != 0) {
      throw new Exception(s"spark-submit failed to complete properly given these arguments: \n\t${argz.mkString(" ")}")
    }
  }

}
