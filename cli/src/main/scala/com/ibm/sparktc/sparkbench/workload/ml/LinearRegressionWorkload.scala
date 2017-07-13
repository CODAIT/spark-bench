package com.ibm.sparktc.sparkbench.workload.ml

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}

object LinearRegressionWorkload extends WorkloadDefaults{
  val name = "linearregresssion"
  def apply(m: Map[String, Any]): Workload = {
    // Stub
    throw new NotImplementedError("Linear regression workloads are not supported at this time")
  }
  // Application parameters #1million points have 200M data size
  val numOfExamples: Int = 40000
  val numOfFeatures: Int = 4
  val eps: Double = 0.5
  val intercepts: Double = 0.1
  val numOfPartitions: Int = 10
  val maxIteration: Int = 3
}
