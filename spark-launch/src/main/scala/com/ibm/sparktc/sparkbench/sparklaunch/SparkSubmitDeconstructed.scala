package com.ibm.sparktc.sparkbench.sparklaunch

import java.util

import com.typesafe.config.{Config, ConfigValueFactory}

import scala.collection.JavaConverters._
import com.ibm.sparktc.sparkbench.sparklaunch.{SparkLaunchDefaults => SLD}
import com.ibm.sparktc.sparkbench.utils.TypesafeAccessories.{configToMapStringAny, splitGroupedConfigToIndividualConfigs}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.optionallyGet

import scala.util.Try



case class SparkSubmitDeconstructedWithSeqs (
                                              sparkSubmitOptions: Map[String, Seq[Any]],
                                              suitesConfig: Config
                                            ) {

  def split(): Seq[SparkSubmitDeconstructed] = {
    val splitMaps: Seq[Map[String, Any]] = splitGroupedConfigToIndividualConfigs(sparkSubmitOptions)
    val asJava: Seq[util.Map[String, Any]] = splitMaps.map(_.asJava)

    asJava.map(SparkSubmitDeconstructed(_, suitesConfig))
  }
}

object SparkSubmitDeconstructedWithSeqs {

  def apply(oneSparkSubmitConfig: Config): SparkSubmitDeconstructedWithSeqs = {
    val suites = oneSparkSubmitConfig.withOnlyPath(SLD.suites)
    val workingConf = oneSparkSubmitConfig.withoutPath(SLD.suites)
    val map: Map[String, Seq[Any]] = configToMapStringAny(workingConf)

    val newMap = extractSparkArgsToHigherLevel(map, workingConf)

    SparkSubmitDeconstructedWithSeqs(
      newMap,
      suites
    )
  }

  private def extractSparkArgsToHigherLevel(map: Map[String, Seq[Any]], workingConf: Config): Map[String, Seq[Any]] = {
    if(map.contains("spark-args")) {
      val mapStripped = map - "spark-args"
      val justSparkArgs = workingConf.withOnlyPath("spark-args").getObject("spark-args").toConfig
      val sparkArgsMap = configToMapStringAny(justSparkArgs)

      val newMap = mapStripped ++ sparkArgsMap
      newMap
    }
    else map
  }
}

case class SparkSubmitDeconstructed (
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

    val suitesParallel = optionallyGetFromJavaMap[Boolean](sparkSubmitOptions,SLD.suitesParallel)

    val conf: Option[util.Map[String, Any]] = optionallyGetFromJavaMap[util.Map[String, Any]](sparkSubmitOptions, SLD.sparkConf)

    val sparkArgs: Option[util.Map[String, Any]] = {

      val asScala = sparkSubmitOptions.asScala


      val filtered = asScala.filterKeys {
        case SLD.sparkConf => false
        case SLD.suitesParallel => false
        case _ => true
      }
      if (filtered.isEmpty) None
      else Some(filtered.asJava)
    }

    SparkSubmitPieces(
      suitesParallel,
      conf,
      sparkArgs,
      suitesConfig
    )
  }
}

case class SparkSubmitPieces (
                               suitesParallel: Option[Boolean],
                               conf: Option[util.Map[String, Any]],
                               sparkArgs: Option[util.Map[String, Any]],
                               suitesConfig: Config
                             ) {
  def reconstruct: Config = {

//    val confToJava = conf match {case Some(m) => Some(m.asJava); case None => None}

    def ifItsThere[A](key: String, option: Option[A]): Option[(String, A)] =
      Try(key -> option.get).toOption

    val mostOfIt = Seq(
      ifItsThere(SLD.suitesParallel, suitesParallel),
      ifItsThere(SLD.sparkConf, conf),
      ifItsThere(SLD.sparkArgs, sparkArgs)
    ).flatten.toMap.asJava

    val sparkSubmitConf = ConfigValueFactory.fromMap(mostOfIt)

    val ret = sparkSubmitConf.withFallback(suitesConfig)
    ret.toConfig
  }
}