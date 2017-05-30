package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.SparkSession

case class SparkContextConf(
                           //todo all the exec mem, cores, etc. will go here
                            master: String,
                            suitesParallel: Boolean,
                            suites: Seq[Suite],
                            sparkConfs: Map[String, String]
                           ) {

  def createSparkContext(): SparkSession = {
    val builder = SparkSession.builder()
      .appName("spark-bench workload")
      .master(master)

    sparkConfs.foldLeft(builder) { case (b, (k, v)) => b.config(k, v) }

    builder.getOrCreate()
  }
}
