package com.ibm.sparktc.sparkbench.cli

import java.io.File
import scala.collection.JavaConverters._
import com.ibm.sparktc.sparkbench.workload.{SparkContextConf, Suite}
import com.typesafe.config.{Config, ConfigFactory, ConfigObject}
import scala.util.Try

object Configurator {

  def apply(file: File): Seq[SparkContextConf] = {
    val config: Config = ConfigFactory.parseFile(file)
    val sparkBenchConfig = config.getObject("spark-bench").toConfig
    val sparkContextConfs = parseSparkContext(sparkBenchConfig)
    sparkContextConfs
  }

  def parseSparkContext(config: Config): List[SparkContextConf] = {
    val sparkContextConfs = getConfigListByName("spark-contexts", config)
    sparkContextConfs.map( sparkContextConf =>
      SparkContextConf(
        master = sparkContextConf.getString("master"),
        suites = getConfigListByName("suites", sparkContextConf).map(parseSuite)
      )
    )
  }

  private def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList(name).asScala
    workloadObjs.map(_.toConfig).toList
  }

  def parseSuite(config: Config): Suite = {
    val descr: Option[String] = Try(config.getString("descr")).toOption
    val parallel: Boolean = Try(config.getBoolean("parallel")).getOrElse(false)
    val repeat: Int = Try(config.getInt("repeat")).getOrElse(1)
    val output: String = config.getString("benchmark-output") // throws exception
    val workloads: Seq[Map[String, Seq[Any]]]  = getConfigListByName("workloads", config).map(parseWorkload)

    Suite.build(
      workloads,
      descr,
      repeat,
      parallel,
      output
    )
  }

  //TODO work on this so you can have values as singles instead of everything a list
  def parseWorkload(config: Config): Map[String, Seq[Any]] = {
    val cfgObj = config.root()
    val unwrapped = cfgObj.unwrapped().asScala.toMap
    val stuff = unwrapped.map(kv => {
      kv._1 -> kv._2.asInstanceOf[java.util.ArrayList[Any]].toArray
    })

    stuff.map(kv => {kv._1 -> kv._2.toSeq})
  }

}
