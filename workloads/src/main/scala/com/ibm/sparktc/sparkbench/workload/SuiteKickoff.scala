package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.workload.mlworkloads.KMeansWorkload
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import com.ibm.sparktc.sparkbench.workload.exercise.{TimedSleepWorkload, TimedSleepWorkloadConf}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions.lit

import scala.collection.parallel.{ForkJoinTaskSupport, ParSeq}
import scala.util.{Failure, Success}

object SuiteKickoff {

  val spark = createSparkContext()

  def apply(confsFromArgs: Seq[Map[String, Seq[Any]]],
            description: Option[String],
            repeat: Int,
            parallel: Boolean,
            benchmarkOutput: String): Unit = {

    val suite: Suite = Suite (
      description,
      repeat,
      parallel,
      benchmarkOutput,
      confsFromArgs
    )

  }

  def run(s: Suite): DataFrame = {
    val workloadConfigs = s.workloadConfigs.map(ConfigCreator.mapToConf(_, spark))

    val dataframes: Seq[DataFrame] = { (0 until s.repeat).flatMap { i =>
        val dfSeqFromOneRun: Seq[DataFrame] = {
          if (s.parallel) {
            val confSeqPar = workloadConfigs.par
            confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(confSeqPar.size))
            val stuff: ParSeq[DataFrame] = confSeqPar.flatMap(kickoff)
            stuff.seq
          }
          else workloadConfigs.flatMap(kickoff)
        }
        dfSeqFromOneRun.map(_.withColumn("run", lit(i)))
      }
    }
    joinDataFrames(dataframes)
  }


  def createSparkContext(): SparkSession = {
    SparkSession
      .builder()
      .appName("spark-bench workload")
      .getOrCreate()
  }

  def kickoff(conf: WorkloadConfig): Option[DataFrame] = {
    conf match {
//      case "kmeans" => Success(new KMeansWorkload(conf, spark).run()).toOption
      case _: TimedSleepWorkloadConf => Success(new TimedSleepWorkload(conf.asInstanceOf[TimedSleepWorkloadConf], spark).run()).toOption
      case _ => Failure(throw new Exception(s"Unrecognized or unimplemented workload")).toOption
    }
  }

  def joinDataFrames(seq: Seq[DataFrame]): DataFrame = {
    if (seq.length == 1) return seq.head
    // Folding left across this sequence should be fine because each DF should only have 1 row
    // Nevarr Evarr do this to legit dataframes that are all like big and stuff
    seq.foldLeft(spark.createDataFrame(spark.sparkContext.emptyRDD[Row], seq.head.schema))(_.union(_))
  }

}
