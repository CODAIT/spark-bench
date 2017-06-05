package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File

import scala.collection.JavaConverters._
import com.typesafe.config.{Config, ConfigFactory, ConfigObject}

import scala.util.Try

object SubmitConfigurator {

  def apply(file: File): Seq[SparkLaunchConf] = {
    val config: Config = ConfigFactory.parseFile(file)
    val sparkBenchConfig = config.getObject("spark-bench").toConfig
    val sparkContextConfs = getConfigListByName("spark-contexts", sparkBenchConfig)
    sparkContextConfs.map(SparkLaunchConf(_, file.getPath))
  }

//  def parseSparkContext(config: Config): List[SparkLaunchConf] = {
//    val sparkContextConfs = getConfigListByName("spark-contexts", config)
//    sparkContextConfs.map { sparkContextConf =>
//      val sparkConfs = Try(sparkContextConf.getObject("conf")).map(toStringMap).getOrElse(Map.empty)
//
//      SparkLaunchConf(
//        master = sparkContextConf.getString("master"),
//       sparkConfs = sparkConfs)
//    }
//  }

  private def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList(name).asScala
    workloadObjs.map(_.toConfig).toList
  }

  def toStringMap(co: ConfigObject): Map[String,String] =
    co.asScala.toMap.mapValues(v => v.unwrapped.toString)
}
