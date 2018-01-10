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

package com.ibm.sparktc.sparkbench.sparklaunch.confparse

import java.io.File

import com.ibm.sparktc.sparkbench.sparklaunch.{SparkLaunchDefaults => SLD}
import com.ibm.sparktc.sparkbench.utils.TypesafeAccessories._
import com.typesafe.config._

import scala.collection.JavaConverters._
import scala.util.Try


/*
          _oo____oo_
         |          |
         |______(*)_|
         /\__----__/\
        /_/()||||()\_\
        |_\ o||||o /_|
        |----Jeep----|
        |_|        |_|
 */

object ConfigWrangler {

  /**
    * Takes in the path to the config file, splits and crossjoins parameters as necessary,
    * returns a sequence of SparkJobConfs ready to be launched.
    * @param path
    * @return a Seq of SparkJobConf ready to be launched
    */

  //TODO wtf we already parsed this file...
  def apply(path: File): Seq[SparkJobConf] = {
    val config: Config = ConfigFactory.parseFile(path)
    val sparkBenchConfig = config.getObject(SLD.topLevelConfObject).toConfig
    val sparkContextConfs  = getListOfSparkSubmits(sparkBenchConfig)

    val processedConfs: Seq[Config] = sparkContextConfs.flatMap(processConfig)
    val scriptsReadyToGo: Seq[SparkJobConf] = processedConfs.map{ oneConf =>
      val wrapped = wrapConf(oneConf)
      SparkJobConf(oneConf, wrapped.root().render(ConfigRenderOptions.concise()))
    }

    scriptsReadyToGo
  }


  /**
    * In order to be parsed correctly, the configs need to be wrapped in an outer
    * `spark-bench = { ... }`
    * @param oneConf a config that has everything but the outer `spark-bench = { ... }`
    * @return that same config wrapped in a `spark-bench = { ... }`
    */
  private def wrapConf(oneConf: Config): Config = {
    val wrapWithSparkSubmit = ConfigFactory.empty.withValue(
      SLD.sparkSubmitObject,
      ConfigValueFactory.fromIterable(Iterable(oneConf.root).asJava)
    )
    val sbConf = ConfigFactory.empty.withValue(SLD.topLevelConfObject, wrapWithSparkSubmit.root)
    sbConf
  }

  /**
    * Take a single spark-submit config and run it through a pipeline that will
    * crossjoin and marshal parameters and ultimately return a sequence of configs.
    * @param config One spark-submit config that
    * @return
    */
  private[sparklaunch] def processConfig(config: Config): Seq[Config] = {
    val a: LaunchConfigDeconstructedWithSeqs = LaunchConfigDeconstructedWithSeqs(config)
    if(a.sparkSubmitOptions.isEmpty) Seq(config)
    else {
      val b: Seq[LaunchConfigDeconstructed] = a.split()
      val c: Seq[SparkSubmitPieces] = b.map(_.splitPieces)
      val d: Seq[Config] = c.map(_.reconstruct)
      d
    }
  }

  /**
    * Utility method to simplify extracting the spark-submit configs from the original config object
    * @param rootConfig
    * @return
    */
  private[sparklaunch] def getListOfSparkSubmits(rootConfig: Config): Seq[Config] = {
    getConfigListByName(s"${SLD.sparkSubmitObject}", rootConfig)
  }

  def isSparkSubmit(map: Map[String, Any]): Boolean = {
    map.contains("spark-home") && !map.contains("url")
  }

  def isLivySubmit(map: Map[String, Any]): Boolean = {
    map.contains("url") && map.contains("poll-seconds")
  }

}
