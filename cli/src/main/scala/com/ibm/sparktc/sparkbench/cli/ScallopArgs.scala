package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.datagen.mlgenerator.KmeansDataGenDefaults
import org.rogach.scallop._

class ScallopArgs(arguments: Array[String]) extends ScallopConf(arguments){

  /*
   * ***********************
   * * DATAGENERATION ARGS *
   * ***********************
   */
  val datagen = new Subcommand("generate-data") {
    val numRows = opt[Int](short = 'r', required = true)
    val numCols = opt[Int](short = 'c', required = true)
    val outputDir = opt[String](short = 'o', required = true)
    val outputFormat = opt[String](short = 'f')

    // DATAGEN
    val kmeans = new Subcommand("kmeans"){
      val k = opt[Int](short = 'k', default = Some(KmeansDataGenDefaults.NUM_OF_CLUSTERS))
      val scaling = opt[Double](short = 'm', default = Some(KmeansDataGenDefaults.SCALING))
      val partitions = opt[Long](short = 's', default = Some(KmeansDataGenDefaults.NUM_OF_PARTITIONS))

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
    val workload = opt[String]()
    val inputDir = opt[String]()
    val outputFormat = opt[String]()
    val outputDir = opt[String]()

    // KMEANS
    val kmeans = new Subcommand("kmeans"){
      val k = opt[Int](short = 'k', default = Some(KmeansDataGenDefaults.NUM_OF_CLUSTERS))
      val maxIterations = opt[Int](short = 'm', default = Some(KmeansDataGenDefaults.MAX_ITERATION))
      val seed = opt[Long](short = 's', default = Some(KmeansDataGenDefaults.SEED))
    }
    addSubcommand(kmeans)


  }

  addSubcommand(workload)

  verify()
}
