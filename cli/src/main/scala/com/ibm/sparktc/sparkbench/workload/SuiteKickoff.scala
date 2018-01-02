/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.utils.SparkFuncs.{writeToDisk, addConfToResults}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions.{col, lit}

import scala.collection.parallel.ForkJoinTaskSupport

/**
    Run each workload in the sequence.
    Run the sequence `repeat` times over.
    If my sequence is Seq(A, B, B, C), it's running serially, and repeat is 2, this will run like:
    A
    B
    B
    C
    ---
    A
    B
    B
    C
    ---
    Done

    As opposed to:
    A
    A
    --
    B
    B
    --
    B
    B
    --
    C
    C
    ---
    Done
*/

object SuiteKickoff {

  def run(s: Suite, spark: SparkSession): Unit = {
    // Translate the maps into runnable workloads
    val workloads: Seq[Workload] = s.workloadConfigs.map(ConfigCreator.mapToConf)

    val dataframes: Seq[DataFrame] = (0 until s.repeat).flatMap { i =>
      // This will produce one DataFrame of one row for each workload in the sequence.
      // We're going to produce one coherent DF later from these
      val dfSeqFromOneRun: Seq[DataFrame] = {
        if (s.parallel) runParallel(workloads, spark)
        else runSerially(workloads, spark)
      }
      // Indicate which run of this suite this was.
      dfSeqFromOneRun.map(_.withColumn("run", lit(i)))
    }

    // getting the Spark confs so we can output them in the results.
    val strSparkConfs = spark.conf.getAll

    // Ah, see, here's where we're joining that series of one-row DFs
    val singleDF = joinDataFrames(dataframes, spark)
    s.description.foreach(println)
    // And now we're going to curry in the results
    val plusSparkConf = addConfToResults(singleDF, strSparkConfs)
    val plusDescription = addConfToResults(plusSparkConf, Map("description" -> s.description)).coalesce(1)
    // And write to disk. We're done with this suite!
    if(s.benchmarkOutput.nonEmpty) writeToDisk(s.benchmarkOutput.get, plusDescription, spark)
  }

  private def runParallel(workloadConfigs: Seq[Workload], spark: SparkSession): Seq[DataFrame] = {
    val confSeqPar = workloadConfigs.par
    confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(confSeqPar.size))
    confSeqPar.map(_.run(spark)).seq
  }

  private def runSerially(workloadConfigs: Seq[Workload], spark: SparkSession): Seq[DataFrame] = {
    workloadConfigs.map(_.run(spark))
  }

  private def joinDataFrames(seq: Seq[DataFrame], spark: SparkSession): DataFrame = {
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
