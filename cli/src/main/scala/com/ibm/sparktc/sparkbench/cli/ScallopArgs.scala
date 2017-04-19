package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
import org.rogach.scallop._

class ScallopArgs(arguments: Array[String]) extends ScallopConf(arguments){

  /*
   * ***********************
   * * DATAGENERATION ARGS *
   * ***********************
   */
  val datagen = new Subcommand("generate-data") {
    // DATAGEN
    val kmeans = new DataGeneratorArgs("kmeans"){
      val k = opt[Int](short = 'k', default = Some(KMeansDefaults.NUM_OF_CLUSTERS))
      val scaling = opt[Double](short = 'm', default = Some(KMeansDefaults.SCALING))
      val partitions = opt[Int](short = 's', default = Some(KMeansDefaults.NUM_OF_PARTITIONS))
    }
    addSubcommand(kmeans)
  }
  addSubcommand(datagen)

  /*
   * *****************
   * * WORKLOAD ARGS *
   * *****************
   */
  val workload = new Subcommand("workload") {

    // KMEANS
    val kmeans = new WorkloadArgs("kmeans"){
      val k = opt[List[Int]](short = 'k', default = Some(List(KMeansDefaults.NUM_OF_CLUSTERS)))
      val maxIterations = opt[List[Int]](short = 'm', default = Some(List(KMeansDefaults.MAX_ITERATION)))
      val seed = opt[List[Long]](short = 's', default = Some(List(KMeansDefaults.SEED)))
    }
    addSubcommand(kmeans)
  }

  addSubcommand(workload)

  verify()
}
