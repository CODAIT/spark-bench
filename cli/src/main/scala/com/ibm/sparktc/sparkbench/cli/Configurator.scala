package com.ibm.sparktc.sparkbench.cli

import scala.collection.JavaConverters._
import com.ibm.sparktc.sparkbench.workload.Suite
import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigValue}

import scala.util.Try


object Configurator {

  def apply(path: String) = {
    val config: Config = ConfigFactory.load(path)
  }

  def parseSparkContext(config: Config): List[SparkContextConf] = {
    val sparkContextConfs = getConfigListByName("spark-contexts", config)
    sparkContextConfs.map( sparkContextConf =>
      SparkContextConf(
        suites = getConfigListByName("suites", sparkContextConf).map(parseSuite)
      )
    )
  }

  private def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList("workloads").asScala
    workloadObjs.map(_.toConfig).toList
  }

  def parseSuite(config: Config): Suite = {
    val descr: Option[String] = Try(config.getString("descr")).toOption
    val parallel: Boolean = Try(config.getBoolean("parallel")).getOrElse(false)
    val repeat: Int = Try(config.getInt("repeat")).getOrElse(1)
    val output: String = config.getString("output") // throws exception
    val workloads = getConfigListByName("workloads", config).map(parseWorkload).toSeq

    Suite(
      descr,
      repeat,
      parallel,
      output,
      workloads
    )
  }

  def parseWorkload(config: Config): Map[String, Seq[Any]] = ???

//  {
//    val stuff: Set[java.util.Map.Entry[String, ConfigValue]] = config.entrySet().asScala.toSet
//    val configValueMap = stuff.map(utilMapEntry => { utilMapEntry.getKey -> utilMapEntry.getValue }).toMap
//
//    val test = configValueMap.head._2
//
//    val vt = test.valueType()
//
//    vt.values()
//
//  }

}
