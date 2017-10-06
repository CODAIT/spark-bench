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

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

import com.ibm.sparktc.sparkbench.utils.TypesafeAccessories._
import com.typesafe.config._
import com.typesafe.config.ConfigRenderOptions.concise

import scala.collection.JavaConverters._
import scala.util.Try
import com.ibm.sparktc.sparkbench.sparklaunch.{SparkLaunchDefaults => SLD}


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
    * Takes in the path to the config file, splits that file to a bunch of files in a folder in /tmp, returns
    * the paths to those files.
    * @param path
    * @return a Seq of the new config files as Strings
    */
  def apply(path: File): Seq[SparkSubmitScriptConf] = {
    val config: Config = ConfigFactory.parseFile(path)
    val sparkBenchConfig = config.getObject(SLD.topLevelConfObject).toConfig
    val sparkContextConfs  = getListOfSparkSubmits(sparkBenchConfig)

    val processedConfs: Seq[Config] = sparkContextConfs.flatMap(processConfig)
    val scriptsReadyToGo: Seq[SparkSubmitScriptConf] = processedConfs.map{ oneConf =>
      val wrapped = wrapConf(oneConf)
      SparkSubmitScriptConf(oneConf, wrapped.root().render(ConfigRenderOptions.concise()))
    }

    scriptsReadyToGo
  }


  private def wrapConf(oneConf: Config): Config = {
    val wrapWithSparkSubmit = ConfigFactory.empty.withValue(
      SLD.sparkSubmitObject,
      ConfigValueFactory.fromIterable(Iterable(oneConf.root).asJava)
    )
    val sbConf = ConfigFactory.empty.withValue(SLD.topLevelConfObject, wrapWithSparkSubmit.root)
    sbConf
  }

  /**
    * Run the confs through the transform pipeline
    * @param config
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
    * The one config file needs to be split up into individual files that can be submitted to spark-submit. This
    * @param rootConf
    * @param oneSparkSubmitConf
    * @return
    */
  private[sparklaunch] def writePartialConfToDisk(rootConf: Config, oneSparkSubmitConf: Config): String = {
    val tmpFile = Files.createTempFile("spark-bench-", ".conf")
    // Write this particular spark-context to a separate file on disk.
    val sbConf = stripSparkSubmitConfPaths(rootConf, oneSparkSubmitConf)
    val output = sbConf.root.render(concise.setJson(false))
    Files.write(tmpFile, output.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    tmpFile.toString
  }

  /**
    * Strip all config paths that are not essential for passing on to the next stage of spark-bench.
    * @param rootConf
    * @param oneSparkSubmitConf
    * @return
    */
  private[sparklaunch] def stripSparkSubmitConfPaths(rootConf: Config, oneSparkSubmitConf: Config): Config = {
    val stripped = oneSparkSubmitConf.withOnlyPath(SLD.suites)
    val suitesParallel: Boolean = Try(oneSparkSubmitConf.getBoolean(SLD.suitesParallel))
      .toOption
      .getOrElse(SLD.suitesParallelDefaultValue)
    val specifyParallelism = stripped.withValue(SLD.suitesParallel, ConfigValueFactory.fromAnyRef(suitesParallel))
    val wrapWithSparkSubmit = ConfigFactory.empty.withValue(
      SLD.sparkSubmitObject,
      ConfigValueFactory.fromIterable(Iterable(specifyParallelism.root).asJava)
    )
    val sbConf = ConfigFactory.empty.withValue(SLD.topLevelConfObject, wrapWithSparkSubmit.root)
    sbConf
  }

  /**
    * Utility method to simplify extracting the spark-submit configs
    * @param rootConfig
    * @return
    */
  private[sparklaunch] def getListOfSparkSubmits(rootConfig: Config): Seq[Config] = {
    getConfigListByName(s"${SLD.sparkSubmitObject}", rootConfig)
  }

}
