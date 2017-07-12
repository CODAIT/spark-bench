package com.ibm.sparktc.sparkbench.workload.ml

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}

object LinearRegressionWorkload extends WorkloadDefaults{
  val name = "linearregresssion"
  def apply(m: Map[String, Any]): Workload = {
    // Stub
    throw new NotImplementedError("Linear regression workloads are not supported at this time")
  }
  // Application parameters #1million points have 200M data size
  val NUM_OF_EXAMPLES: Int = 40000
  val NUM_OF_FEATURES: Int = 4
  val EPS: Double = 0.5
  val INTERCEPTS: Double = 0.1
  val NUM_OF_PARTITIONS: Int = 10
  val MAX_ITERATION: Int = 3
  //  val SPARK_STORAGE_MEMORYFRACTION = 0.5
}
