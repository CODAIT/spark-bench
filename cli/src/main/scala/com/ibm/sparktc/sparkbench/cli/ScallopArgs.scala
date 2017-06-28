package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.utils.{KMeansDefaults, TimedSleepDefaults}
import org.rogach.scallop._
import java.io.File

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
      val k = opt[Int](short = 'k', default = Some(KMeansDefaults.NUM_OF_CLUSTERS))
      val scaling = opt[Double](short = 's', default = Some(KMeansDefaults.SCALING))
      val partitions = opt[Int](short = 'p', default = Some(KMeansDefaults.NUM_OF_PARTITIONS))
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
    val kmeansStr: String = "kmeans"
    val kmeans = new SuiteArgs(kmeansStr){
      val k = opt[List[Int]](short = 'k', default = Some(List(KMeansDefaults.NUM_OF_CLUSTERS)))
      val maxIterations = opt[List[Int]](short = 'm', default = Some(List(KMeansDefaults.MAX_ITERATION)))
      val seed = opt[List[Long]](short = 'e', default = Some(List(KMeansDefaults.SEED)))
    }
    addSubcommand(kmeans)

    // TIMED SLEEP
    val timedsleep = new SuiteArgs("timedsleep"){
      val partitions = opt[List[Int]](short = 'p', default = Some(List(TimedSleepDefaults.PARTITIONS)), descr = "how many partitions to spawn")
      val sleepMS = opt[List[Long]](short = 't', default = Some(List(TimedSleepDefaults.SLEEPMS)), descr = "amount of time a thread will sleep, in milliseconds")
    }
    addSubcommand(timedsleep)

    // LOGISTIC REGRESSION
    val lrStr: String = "lr-bml"
    val lr = new SuiteArgs(lrStr){
      val trainfile = opt[List[String]](short = 't', default = None)
      val testfile = opt[List[String]](short = 'r', default = None)
      val numpartitions = opt[List[Int]](short = 'p', default = Some(List(32)))
    }
    addSubcommand(lr)

    // STRING RETURNER
    val helloString = new SuiteArgs("hellostring") {
      val str = opt[List[String]](short = 's', default = Some(List("Hello, World!")), required = true)
    }
    addSubcommand(helloString)
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
