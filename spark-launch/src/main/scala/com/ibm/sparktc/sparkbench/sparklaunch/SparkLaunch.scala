package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File
import scala.sys.process._
import scala.collection.parallel.ForkJoinTaskSupport
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow
import scala.collection.JavaConverters._
import com.typesafe.config.{Config, ConfigFactory, ConfigObject}
import scala.util.Try

object SparkLaunch extends App {


  override def main(args: Array[String]): Unit = {
    println("lol")
    println(s"these are my argz: ${args.mkString(", ")}")
    assert(args.nonEmpty)
    val path = args.head
    val (confSeq: Seq[SparkLaunchConf], parallel: Boolean ) = mkConfs(new File(path))

    run(confSeq, parallel)
  }

  def mkConfs(file: File): (Seq[SparkLaunchConf], Boolean) = {
    val config: Config = ConfigFactory.parseFile(file)
    val sparkBenchConfig = config.getObject("spark-bench").toConfig
    val confs: Seq[SparkLaunchConf] = ConfigWrangler(file)
    val parallel = Try(sparkBenchConfig.getBoolean("spark-contexts-parallel")).getOrElse(false)
    (confs, parallel)
  }

  private def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList(name).asScala
    workloadObjs.map(_.toConfig).toList
  }

  def run(confSeq: Seq[SparkLaunchConf], parallel: Boolean): Unit = {
    if(parallel) {
      val confSeqPar = confSeq.par
      //TODO address the concern that this could be confSeqPar.size threads for EACH member of ParSeq
      confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(confSeqPar.size))
      confSeqPar.foreach(launch)
    }
    else {
      confSeq.foreach(launch)
    }
  }

  def launch(conf: SparkLaunchConf): Unit = {
    val argz: Array[String] = conf.toSparkArgs
    println(s"argz are: ${argz.mkString(", ")}")
    val sparkHome = getOrThrow(sys.env.get("SPARK_HOME"))
    val returnCode: Int = s"""$sparkHome/bin/spark-submit ${argz.mkString(" ")}""".!
    if (returnCode != 0) {
      throw new Exception(s"spark-submit failed to complete properly given these arguments: \n\t${argz.mkString(" ")}")
    }
  }
}
