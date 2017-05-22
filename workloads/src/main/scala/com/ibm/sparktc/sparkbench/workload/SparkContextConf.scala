package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.sql.SparkSession

case class SparkContextConf(
                           //todo all the exec mem, cores, etc. will go here
                            master: String,
                            suitesParallel: Boolean,
                            suites: Seq[Suite]
                           ) {

  def createSparkContext(): SparkSession = {
    SparkSession
      .builder()
      .appName("spark-bench workload")
      .master(master)
      .getOrCreate()
  }

}
