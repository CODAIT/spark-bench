package com.ibm.sparktc.sparkbench.utils

object KMeansDefaults {
  // The parameters for data generation. 100 million points (aka rows) roughly produces 36GB data size
  val K: Int = 2
  val DIMENSIONS: Int = 20
  val SCALING: Double = 0.6
  val NUM_OF_PARTITIONS: Int = 2

  // Params for workload, in addition to some stuff up there ^^
  val MAX_ITERATION: Int = 2
  val SEED: Long = 127L
}

object LinearRegressionDefaults {
  // Application parameters #1million points have 200M data size
  val NUM_OF_EXAMPLES: Int = 40000
  val NUM_OF_FEATURES: Int = 4
  val EPS: Double = 0.5
  val INTERCEPTS: Double = 0.1
  val NUM_OF_PARTITIONS: Int = 10
  val MAX_ITERATION: Int = 3
//  val SPARK_STORAGE_MEMORYFRACTION = 0.5
}

object TimedSleepDefaults {
  val PARTITIONS: Int = 48
  val SLEEPMS: Long = 12000L

}