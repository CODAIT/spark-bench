package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.workload.SuiteKickoff.kickoff
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.parallel.{ForkJoinTaskSupport, ParSeq}

object SparkContextKickoff {

  def run(seq: Seq[SparkContextConf]): Unit = {
    seq.foreach(contextConf => {
      val spark = contextConf.createSparkContext()
      if(contextConf.suitesParallel) {
        runSuitesInParallel(contextConf.suites, spark)
      }
      else {
        runSuitesSerially(contextConf.suites, spark)
      }
    })
  }

  def runSuitesInParallel(suiteSeq: Seq[Suite], spark: SparkSession): Unit = {
    val parallelSeq = suiteSeq.par
    //TODO address the concern that this could be parallelSeq.size threads for EACH member of ParSeq
    parallelSeq.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(parallelSeq.size))
    parallelSeq.foreach(suite => SuiteKickoff.run(suite, spark))
  }

  def runSuitesSerially(suiteSeq: Seq[Suite], spark: SparkSession): Unit = {
    suiteSeq.foreach(suite => {
      SuiteKickoff.run(suite, spark)
    })
  }

}
