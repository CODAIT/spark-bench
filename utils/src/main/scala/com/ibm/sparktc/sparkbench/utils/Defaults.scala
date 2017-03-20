package com.ibm.sparktc.sparkbench.utils

object KMeansDefaults {
  // The parameters for data generation. 100 million points (aka rows) roughly produces 36GB data size
  val NUM_OF_CLUSTERS: Int = 2
  val DIMENSIONS: Int = 20
  val SCALING: Double = 0.6
  val NUM_OF_PARTITIONS: Int = 2

  // Params for workload, in addition to some stuff up there ^^
  val MAX_ITERATION: Int = 2
  val SEED: Long = 127L
}

object SomeOtherDefaults{

}