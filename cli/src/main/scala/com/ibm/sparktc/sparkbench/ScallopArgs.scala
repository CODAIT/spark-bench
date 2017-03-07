package com.ibm.sparktc.sparkbench

import org.rogach.scallop.{ScallopConf, Subcommand}

class ScallopArgs(arguments: Array[String]) extends ScallopConf(arguments){

  // Data Generation
  val datagen = new Subcommand("generate-data") {

  }

  val workload = new Subcommand("workload")
}
