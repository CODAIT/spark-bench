package com.ibm.sparktc.sparkbench.cli

import java.io.File
import java.util

import com.ibm.sparktc.sparkbench.utils.SparkBenchException

import scala.collection.JavaConverters._
import com.ibm.sparktc.sparkbench.workload.{MultiSuiteRunConfig, Suite}
import com.typesafe.config.{Config, ConfigFactory, ConfigObject}

import scala.util.Try

/*

spark-bench = {
  spark-submit-config = [{

    // All data generation done serially and completes before benchmarks start.
    workload-suites = [
      {
        descr = "OMG I'm generating data!!!"
        benchmark-output = "console"
        workloads = [{
          name = "generate-data-kmeans"
          rows = 10
          cols = 10
          output = "/tmp/spark-bench-demo/kmeans-01.csv"
        }]
      },
      {
        descr = "One run of SparkPi and that's it!"
        benchmark-output = "console"
        workloads = [
          {
            name = "sparkpi"
            slices = 10
          }
        ]
      },
      {
        descr = "Now let's try two runs in parallel and see how that effects performance"
        benchmark-output = "console"
        parallel = true
        workloads = [
          {
            name = "sparkpi"
            slices = [10, 10]
          }
        ]
      }
    ]
  }]
}
 */

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
    val workloads: Seq[Map[String, Seq[Any]]]  = getConfigListByName("workloads", config).map(configToMap)

    Suite.build(
      workloads,
      descr,
      repeat,
      parallel,
      output
    )
  }

  def configToMap(config: Config): Map[String, Seq[Any]] = {
    val cfgObj = config.root()
    val unwrapped = cfgObj.unwrapped().asScala.toMap
    val stuff: Map[String, Seq[Any]] = unwrapped.map(kv => {
      val newValue: Seq[Any] = kv._2 match {
        case al: util.ArrayList[Any] => al.asScala
        case b: Any => Seq(b)
//        case _ => throw SparkBenchException(s"Key ${kv._1} with value ${kv._2} had an unexpected type: ${kv._2.getClass.toString}")
      }
      kv._1 -> newValue
    })
    stuff
  }

}
