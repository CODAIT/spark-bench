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

import org.apache.spark.sql.SparkSession

import scala.collection.parallel.ForkJoinTaskSupport

//TODO this is probably the best place to kickoff the data generation suites and then the workloads

object MultipleSuiteKickoff {
  def run(seq: Seq[MultiSuiteRunConfig]): Unit = seq.foreach { contextConf =>
    val spark = createSparkContext
    if (contextConf.suitesParallel) runSuitesInParallel(contextConf.suites, spark)
    else runSuitesSerially(contextConf.suites, spark)
  }

  private def runSuitesInParallel(suiteSeq: Seq[Suite], spark: SparkSession): Unit = {
    val parallelSeq = suiteSeq.par
    //TODO address the concern that this could be parallelSeq.size threads for EACH member of ParSeq
    parallelSeq.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(parallelSeq.size))
    parallelSeq.foreach(suite => SuiteKickoff.run(suite, spark))
  }

  private def runSuitesSerially(suiteSeq: Seq[Suite], spark: SparkSession): Unit =
    suiteSeq.foreach(SuiteKickoff.run(_, spark))

  private def createSparkContext: SparkSession = SparkSession.builder.getOrCreate
}
