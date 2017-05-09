package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.workload.mlworkloads.{KMeansWorkload, KMeansWorkloadConfig}
import com.ibm.sparktc.sparkbench.workload.exercise.{TimedSleepWorkload, TimedSleepWorkloadConf}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions.{col, lit}

import scala.collection.parallel.{ForkJoinTaskSupport, ParSeq}
import scala.util.{Failure, Success}

object SuiteKickoff {

  def run(s: Suite, spark: SparkSession): DataFrame = {
    val workloadConfigs = s.workloadConfigs.map(ConfigCreator.mapToConf(_, spark))

    //TODO reading this makes me sad :(
    val dataframes: Seq[DataFrame] = { (0 until s.repeat).flatMap { i =>
        val dfSeqFromOneRun: Seq[DataFrame] = {
          if (s.parallel) {
            val confSeqPar = workloadConfigs.par
            //TODO address the concern that this could be confSeqPar.size threads for EACH member of ParSeq
            confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(confSeqPar.size))
            val stuff: ParSeq[DataFrame] = confSeqPar.flatMap(kickoff(_, spark))
            stuff.seq
          }
          else workloadConfigs.flatMap(kickoff(_, spark))
        }
        dfSeqFromOneRun.map(_.withColumn("run", lit(i)))
      }
    }
    joinDataFrames(dataframes, spark)
  }




  def kickoff(conf: WorkloadConfig, spark: SparkSession): Option[DataFrame] = {
    //TODO this needs refactoring to get rid of boilerplate, design for the custom case
    conf match {
      case _: KMeansWorkloadConfig => Success(new KMeansWorkload(conf.asInstanceOf[KMeansWorkloadConfig], spark).run()).toOption
      case _: TimedSleepWorkloadConf => Success(new TimedSleepWorkload(conf.asInstanceOf[TimedSleepWorkloadConf], spark).run()).toOption
      case _ => Failure(throw new Exception(s"Unrecognized or unimplemented workload")).toOption
    }
  }

  def joinDataFrames(seq: Seq[DataFrame], spark: SparkSession): DataFrame = {
    if (seq.length == 1) return seq.head

    val seqOfColNames = seq.map(_.columns.toSet)
    val allTheColumns = seqOfColNames.foldLeft(Set[String]())(_ ++ _)

    def expr(myCols: Set[String], allCols: Set[String]) = {
      allCols.toList.map {
        case x if myCols.contains(x) => col(x)
        case x => lit(null).as(x)
      }
    }

    val seqFixedDfs = seq.map(df  => df.select(expr(df.columns.toSet, allTheColumns):_*))

    // Folding left across this sequence should be fine because each DF should only have 1 row
    // Nevarr Evarr do this to legit dataframes that are all like big and stuff
    seqFixedDfs.foldLeft(spark.createDataFrame(spark.sparkContext.emptyRDD[Row], seqFixedDfs.head.schema))( _ union _ )
  }

}
