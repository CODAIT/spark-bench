package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.workload.mlworkloads.KMeansWorkload
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.parallel.{ForkJoinTaskSupport, ParSeq}
import scala.util.{Failure, Success}

object WorkloadKickoff {

  val spark = createSparkContext()

  def apply(conf: WorkloadConfigRoot): Unit = {
    val splitOutConfigs: Seq[WorkloadConfig] = conf.split()
    val results = run(splitOutConfigs, conf)
    writeToDisk(data = results, outputDir = conf.outputDir)
  }

  def run(seq: Seq[WorkloadConfig], conf: WorkloadConfigRoot): DataFrame = {
    if(conf.parallel) {
      val confSeqPar = seq.par
      confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(seq.size))
      val stuff: ParSeq[DataFrame] = confSeqPar.flatMap( kickoff )
      joinDataFrames(stuff.seq)
    }
    else {
      val stuff: Seq[DataFrame] = seq.flatMap( kickoff )
      joinDataFrames(stuff)
    }
  }

  def createSparkContext(): SparkSession = {
    SparkSession
      .builder()
      .appName("spark-bench workload")
      .getOrCreate()
  }

  def kickoff(conf: WorkloadConfig): Option[DataFrame] = {
    println(s"\t\t\t\t\t\t\t\tTHIS IS THE CONFIG I'M WORKING ON NOW:\n\t\t\t\t\t\t\t\t\t$conf")
    conf.name.toLowerCase match {
      case "kmeans" => Success(new KMeansWorkload(conf, spark).run()).toOption
      case _ => Failure(new Exception(s"Unrecognized data generator name: ${conf.name}")).toOption
    }
  }

  def joinDataFrames(seq: Seq[DataFrame]): DataFrame = {
    if (seq.length == 1) return seq.head
    // Folding left across this sequence should be fine because each DF should only have 1 row
    seq.foldLeft(seq.head)(_.join(_)) //todo test the heck out of this
  }

}
