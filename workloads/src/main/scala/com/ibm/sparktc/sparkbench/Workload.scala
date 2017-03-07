package com.ibm.sparktc.sparkbench

abstract class Workload(conf: WorkloadConfig) {
  println(s"I'm doing a workload: $conf")

  def load(path: String): Unit = {
    //TODO implementation in Spark that returns a DataSet
  }

  def run(): Unit = {
    //TODO implementation in Spark that returns a results DataSet
  }

  def saveResults(format: String, path: String): Unit = {
    //TODO implementation in Spark that saves the result set in the specified format in the specified place
  }
}
