package com.ibm.sparktc.sparkbench.cli

import java.io.File
import java.util

import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.utils.TypesafeAccessories.configToMapStringAny

import scala.collection.JavaConverters._
import com.ibm.sparktc.sparkbench.workload.{MultiSuiteRunConfig, Suite}
import com.typesafe.config.{Config, ConfigFactory, ConfigObject}

import scala.util.Try

object Configurator {

  def apply(file: File): Seq[MultiSuiteRunConfig] = {
    val config: Config = ConfigFactory.parseFile(file)
    val sparkBenchConfig = config.getObject("spark-bench").toConfig
    val sparkContextConfs = parseSparkBenchRunConfig(sparkBenchConfig)
    sparkContextConfs
  }

  private def parseSparkBenchRunConfig(config: Config): Seq[MultiSuiteRunConfig] = {
    val sparkContextConfs = getConfigListByName("spark-submit-config", config)
    val workloadConfs = sparkContextConfs.map { sparkContextConf => {
        MultiSuiteRunConfig(
          suitesParallel = Try(sparkContextConf.getBoolean("suites-parallel")).getOrElse(false),
          suites = getConfigListByName("workload-suites", sparkContextConf).map(parseSuite)
        )
      }
    }
    workloadConfs
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
    val workloads: Seq[Map[String, Seq[Any]]]  = getConfigListByName("workloads", config).map(configToMapStringAny)

    Suite.build(
      workloads,
      descr,
      repeat,
      parallel,
      output
    )
  }



}
