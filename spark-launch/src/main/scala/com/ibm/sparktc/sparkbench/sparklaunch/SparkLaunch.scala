package com.ibm.sparktc.sparkbench.sparklaunch

import com.typesafe.config._
import java.io.File
import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.JavaConverters._
import scala.sys.process._
import scala.util.Try

object SparkLaunch extends App {

  override def main(args: Array[String]): Unit = {
    assert(args.nonEmpty)
    val path = args.head
    val (confSeq: Seq[(SparkLaunchConf, String)], parallel: Boolean) = mkConfs(new File(path))
    run(confSeq.map(_._1), parallel)
    rmTmpFiles(confSeq.map(_._2))
  }

  def mkConfs(file: File): (Seq[(SparkLaunchConf, String)], Boolean) = {
    val config: Config = ConfigFactory.parseFile(file)
    val sparkBenchConfig = config.getObject("spark-bench").toConfig
    val confs: Seq[(SparkLaunchConf, String)] = ConfigWrangler(file)
    val parallel = Try(sparkBenchConfig.getBoolean("spark-submit-parallel")).getOrElse(false)
    (confs, parallel)
  }

  private def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList(name).asScala
    workloadObjs.map(_.toConfig).toList
  }

  def run(confSeq: Seq[SparkLaunchConf], parallel: Boolean): Unit = {
    if (parallel) {
      val confSeqPar = confSeq.par
      //TODO address the concern that this could be confSeqPar.size threads for EACH member of ParSeq
      confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(confSeqPar.size))
      confSeqPar.foreach(launch)
    } else confSeq.foreach(launch)
  }

  def launch(conf: SparkLaunchConf): Unit = {
    val argz: Array[String] = conf.toSparkArgs
    val submitProc = Process(Seq(s"${conf.sparkHome}/bin/spark-submit") ++ argz, None, "SPARK_HOME" -> conf.sparkHome)
    println(" *** SPARK-SUBMIT: " + submitProc.toString)
    if (submitProc.! != 0) {
      throw new Exception(s"spark-submit failed to complete properly given these arguments: \n\t${argz.mkString(" ")}")
    }
  }

  private[sparklaunch] def rmTmpFiles(fns: Seq[String]): Unit = fns.foreach { fn =>
    try {
      val f = new File(fn)
      if (f.exists) f.delete
    } catch { case e: Throwable => println(s"failed to delete $fn", e) }
  }
}
