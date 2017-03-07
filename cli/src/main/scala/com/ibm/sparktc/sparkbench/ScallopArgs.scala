package com.ibm.sparktc.sparkbench

import org.rogach.scallop.{ScallopConf, Subcommand}

class ScallopArgs(arguments: Array[String]) extends ScallopConf(arguments){

  /*
   * ***********************
   * * DATAGENERATION ARGS *
   * ***********************
   */
  val datagen = new Subcommand("generate-data") {

  }

  /*
   * *****************
   * * WORKLOAD ARGS *
   * *****************
   */
  val workload = new Subcommand("workload") {

  }
}
