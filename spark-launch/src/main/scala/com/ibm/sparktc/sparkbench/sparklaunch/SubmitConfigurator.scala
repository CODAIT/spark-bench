package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File

import com.ibm.sparktc.sparkbench.sparklaunch

import scala.collection.JavaConverters._
import com.typesafe.config.{Config, ConfigFactory, ConfigObject}

import scala.util.Try

object SubmitConfigurator {

  def apply(file: File): Seq[SparkLaunchConf] = {
    val config: Config = ConfigFactory.parseFile(file)
    val sparkBenchConfig = config.getObject("spark-bench").toConfig
    val sparkContextConfs = parseSparkContext(file, sparkBenchConfig)
    sparkContextConfs
  }

  def parseSparkContext(file: File, config: Config): List[SparkLaunchConf] = {
    val sparkContextConfs = getConfigListByName("spark-contexts", config)
    sparkContextConfs.map( sparkContextConf => SparkLaunchConf(sparkContextConf, file.getPath)

    )
  }

  private def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList(name).asScala
    workloadObjs.map(_.toConfig).toList
  }

}
