package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions.{col, lit}

import scala.collection.parallel.ForkJoinTaskSupport

object SuiteKickoff {

  def run(s: Suite, spark: SparkSession): Unit = {
    val workloadConfigs = s.workloadConfigs.map(ConfigCreator.mapToConf)

    //TODO reading this makes me sad :(
    val dataframes: Seq[DataFrame] = (0 until s.repeat).flatMap { i =>
      val dfSeqFromOneRun: Seq[DataFrame] = if (s.parallel) {
        val confSeqPar = workloadConfigs.par
        //TODO address the concern that this could be confSeqPar.size threads for EACH member of ParSeq
        confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(confSeqPar.size))
        confSeqPar.map(_.run(spark)).seq
      } else {
        workloadConfigs.map(_.run(spark))
      }

      dfSeqFromOneRun.map(_.withColumn("run", lit(i)))
    }

    //TODO this is where you should add spark and system conf

    val strSparkConfs = spark.conf.getAll

    val singleDF = joinDataFrames(dataframes, spark)
    s.description.foreach(println)
    val plusSparkConf = addConfToResults(singleDF, strSparkConfs)
    val plusDescription = addConfToResults(plusSparkConf, Map("description" -> s.description))
    writeToDisk(s.benchmarkOutput, plusDescription, spark)
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

  def addConfToResults(df: DataFrame, m: Map[String, Any]): DataFrame = {
    def dealWithNones(a: Any): Any = a match {
      case None => ""
      case Some(b) => b
      case _ => a
    }

    var ddf: DataFrame = df
    m.foreach( keyValue => ddf = ddf.withColumn(keyValue._1, lit(dealWithNones(keyValue._2))) )
    ddf
  }

}
