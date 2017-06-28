package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.workload.ml._
import org.rogach.scallop._
import java.io.File

import com.ibm.sparktc.sparkbench.workload.exercise.PartitionAndSleepWorkload
import org.rogach.scallop.exceptions._

class ScallopArgs(arguments: Array[String]) extends ScallopConf(arguments){

  shortSubcommandsHelp()

  version("spark-bench for Spark 2.1.0")

  banner(
    s"""
       |Usage:
       |      spark-bench generate-data <GENERATE DATA ARGS>   ->   see "spark-bench generate-data --help" for more info
       |      spark-bench workload <WORKLOAD ARGS>             ->   see "spark-bench workload --help" for more info
       |      spark-bench /path/to/configuration/file.conf     ->   run spark-bench from a configuration file
       |
       |More options:
     """.stripMargin)

  footer("\nSee the README and project wiki for more documentation.")

  val confFile = trailArg[File](required = false, descr = "Path to a spark-bench configuration file. See README and examples for more on configuration files.")

  val dryRun = opt[Boolean](required = false, default = Some(false), descr = "[EXPERIMENTAL] Prints the configuration of each workload that will run but does not actually run them.")


  /*
   * ***********************
   * * DATAGENERATION ARGS *
   * ***********************
   */
  val datagen = new Subcommand("generate-data") {
    // DATAGEN
    val kmeans = new DataGeneratorArgs("kmeans"){
      val k = opt[Int](short = 'k', default = Some(KMeansWorkload.NUM_OF_CLUSTERS))
      val scaling = opt[Double](short = 's', default = Some(KMeansWorkload.SCALING))
      val partitions = opt[Int](short = 'p', default = Some(KMeansWorkload.NUM_OF_PARTITIONS))
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
    val kmeans = KMeansWorkload.subcommand
    addSubcommand(kmeans)
    val timedsleep = PartitionAndSleepWorkload.subcommand
    addSubcommand(timedsleep)
    val lr = LogisticRegressionWorkload.subcommand
    addSubcommand(lr)
}

addSubcommand(workload)

override def onError(e: Throwable): Unit = e match {
case ScallopException(message) => throw e
case _ => super.onError(e)
}

//
//  override def printHelp(): Unit = {
//
//  }

validateFileExists(confFile)
validateFileIsFile(confFile)
verify()
}
