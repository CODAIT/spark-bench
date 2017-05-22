package com.ibm.sparktc.sparkbench.testfixtures

import org.apache.spark.sql.SparkSession

object SparkSessionProvider {
  val spark: SparkSession =
    SparkSession.builder()
      .master("local[2]")
      .getOrCreate()

  spark.conf.set("mapreduce.fileoutputcommitter.algorithm.version", "2")
  spark.conf.set("speculation", "false")
}
