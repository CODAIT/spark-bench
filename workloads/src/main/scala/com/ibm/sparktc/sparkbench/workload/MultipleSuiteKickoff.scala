package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.SparkSession

import scala.collection.parallel.ForkJoinTaskSupport

//TODO this is probably the best place to kickoff the data generation suites and then the workloads

object MultipleSuiteKickoff {

  def run(seq: Seq[MultiSuiteRunConfig]): Unit = {
    seq.foreach(contextConf => {
      val spark = createSparkContext()
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

  def createSparkContext(): SparkSession = {
    SparkSession.builder().getOrCreate()
  }
}
