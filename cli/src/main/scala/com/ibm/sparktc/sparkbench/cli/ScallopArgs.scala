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
    val numRows = opt[Int](short = 'r', required = true)
    val numCols = opt[Int](short = 'c', required = true)
    val outputDir = opt[String](short = 'o', required = true)
    val outputFormat = opt[String](short = 'f', default = None)

    // DATAGEN
    val kmeans = new Subcommand("kmeans"){
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
    val runs = opt[Int](short = 'n', required = false, default = Some(1), descr = "Number of times each workload variation is run")
    val parallel = opt[Boolean]("parallel", descr = "Specify this option to have the workloads run on the same SparkSession", noshort = true)
    val inputDir = opt[List[String]](short = 'i', required = true)
    val inputFormat = opt[String](required = false, default = Some("csv"))
    val outputDir = opt[String](short = 'o', required = true)
    val outputFormat = opt[String](short = 'f', default = Some("csv"))
    val workloadResultsOutputDir = opt[String](noshort = true, required = false, default = None)
    val workloadResultsOutputFormat = opt[String](noshort = true, required = false, default = None)


    // KMEANS
    val kmeans = new Subcommand("kmeans"){
      val k = opt[List[Int]](short = 'k', default = Some(List(KMeansDefaults.NUM_OF_CLUSTERS)))
      val maxIterations = opt[List[Int]](short = 'm', default = Some(List(KMeansDefaults.MAX_ITERATION)))
      val seed = opt[List[Long]](short = 's', default = Some(List(KMeansDefaults.SEED)))
    }
    addSubcommand(kmeans)
  }

  addSubcommand(workload)

  verify()
}
