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

import java.util

import com.ibm.sparktc.sparkbench.sparklaunch.{SparkLaunchDefaults => SLD}
import com.ibm.sparktc.sparkbench.utils.TypesafeAccessories.{configToMapStringSeqAny, splitGroupedConfigToIndividualConfigs}
import com.typesafe.config.{Config, ConfigValueFactory}

import scala.collection.JavaConverters._
import scala.util.Try

case class LaunchConfigDeconstructedWithSeqs(
                                              sparkSubmitOptions: Map[String, Seq[Any]],
                                              suitesConfig: Config
                                            ) {

  def split(): Seq[LaunchConfigDeconstructed] = {
    val splitMaps: Seq[Map[String, Any]] = splitGroupedConfigToIndividualConfigs(sparkSubmitOptions)
    val asJava: Seq[util.Map[String, Any]] = splitMaps.map(_.asJava)

    asJava.map(LaunchConfigDeconstructed(_, suitesConfig))
  }
}

object LaunchConfigDeconstructedWithSeqs {

  def apply(oneSparkSubmitConfig: Config): LaunchConfigDeconstructedWithSeqs = {
    val suites = oneSparkSubmitConfig.withOnlyPath(SLD.suites)
    val workingConf = oneSparkSubmitConfig.withoutPath(SLD.suites)
    val map: Map[String, Seq[Any]] = configToMapStringSeqAny(workingConf)

    val newMap = extractSparkArgsToHigherLevel(map, workingConf)

    LaunchConfigDeconstructedWithSeqs(
      newMap,
      suites
    )
  }

  private def extractSparkArgsToHigherLevel(map: Map[String, Seq[Any]], workingConf: Config): Map[String, Seq[Any]] = {
    if(map.contains("spark-args")) {
      val mapStripped = map - "spark-args"
      val justSparkArgs = workingConf.withOnlyPath("spark-args").getObject("spark-args").toConfig
      val sparkArgsMap = configToMapStringSeqAny(justSparkArgs)

      val newMap = mapStripped ++ sparkArgsMap
      newMap
    }
    else map
  }
}

case class LaunchConfigDeconstructed(
                                      sparkSubmitOptions: util.Map[String, Any],
                                      suitesConfig: Config
                                    ) {
  def splitPieces: SparkSubmitPieces = {

    def optionMapToJava[K, V](m: Option[Map[K, V]]) = m match {
      case Some(map) => Some(map.asJava)
      case _ => None
    }

    def optionallyGetFromJavaMap[A](m: util.Map[String, Any], key: String): Option[A] = {
      if (m.containsKey(key))
        Some(m.get(key).asInstanceOf[A])
      else None
    }

    val sparkHome = optionallyGetFromJavaMap[String](sparkSubmitOptions, SLD.sparkHome)
    val suitesParallel = optionallyGetFromJavaMap[Boolean](sparkSubmitOptions, SLD.suitesParallel)
    val livyConf = optionallyGetFromJavaMap[util.HashMap[String, Any]](sparkSubmitOptions, SLD.livy)
    val conf: Option[util.Map[String, Any]] = optionallyGetFromJavaMap[util.Map[String, Any]](sparkSubmitOptions, SLD.sparkConf)
    val sparkBenchJar = optionallyGetFromJavaMap[String](sparkSubmitOptions, SLD.sparkBenchJar)
    val enableHive = optionallyGetFromJavaMap[Boolean](sparkSubmitOptions, SLD.enableHive)

    val sparkArgs: Option[util.Map[String, Any]] = {
      val asScala = sparkSubmitOptions.asScala
      val filtered = asScala.filterKeys {
        case SLD.sparkConf => false
        case SLD.suitesParallel => false
        case SLD.enableHive => false
        case SLD.sparkHome => false
        case SLD.sparkBenchJar => false
        case SLD.livy => false
        case _ => true
      }

      if (filtered.isEmpty) None
      else Some(filtered.asJava)
    }

    SparkSubmitPieces(
      sparkHome,
      suitesParallel,
      livyConf,
      conf,
      sparkArgs,
      suitesConfig,
      sparkBenchJar,
      enableHive
    )
  }
}

case class SparkSubmitPieces (
                               sparkHome: Option[String],
                               suitesParallel: Option[Boolean],
                               livyConf: Option[util.Map[String, Any]],
                               conf: Option[util.Map[String, Any]],
                               sparkArgs: Option[util.Map[String, Any]],
                               suitesConfig: Config,
                               sparkBenchJar: Option[String],
                               enableHive: Option[Boolean]
                             ) {
  def reconstruct: Config = {

    def ifItsThere[A](key: String, option: Option[A]): Option[(String, A)] =
      Try(key -> option.get).toOption

    val mostOfIt = Seq(
      ifItsThere(SLD.sparkHome, sparkHome),
      ifItsThere(SLD.suitesParallel, suitesParallel),
      ifItsThere(SLD.livy, livyConf),
      ifItsThere(SLD.sparkConf, conf),
      ifItsThere(SLD.sparkArgs, sparkArgs),
      ifItsThere(SLD.sparkBenchJar, sparkBenchJar),
      ifItsThere(SLD.enableHive, enableHive)
    ).flatten.toMap.asJava

    val sparkSubmitConf = ConfigValueFactory.fromMap(mostOfIt)

    val ret = sparkSubmitConf.withFallback(suitesConfig)
    ret.toConfig
  }
}