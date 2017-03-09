package com.ibm.sparktc.sparkbench.cli

import org.rogach.scallop._

class ScallopArgs(arguments: Array[String]) extends ScallopConf(arguments){

  /*
   * ***********************
   * * DATAGENERATION ARGS *
   * ***********************
   */
  val datagen = new Subcommand("generate-data") {
    val numRows = opt[Int](short = 'r')
    val outputDir = opt[String](short = 'o')
    val kmeans = new Subcommand("kmeans")
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
  }

  addSubcommand(workload)

  verify()
}
