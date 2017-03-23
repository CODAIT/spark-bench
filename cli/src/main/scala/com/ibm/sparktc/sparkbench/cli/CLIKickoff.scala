package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.datageneration.DataGenerationKickoff
import com.ibm.sparktc.sparkbench.workload.WorkloadKickoff

object CLIKickoff {

  def startWorkload(scallopArgs: ScallopArgs): Unit = {
    val conf = ArgsParser.parseWorkload(scallopArgs)
    println("OMFG I'M RUNNING A WORKLOAD")
    WorkloadKickoff(conf)
  }

  def startDataGen(scallopArgs: ScallopArgs): Unit = {
    val conf = ArgsParser.parseDataGen(scallopArgs)
    println("OMFG I'M TOTES GENERATING DATA")
    DataGenerationKickoff(conf)
  }

  def main(args: Array[String]): Unit = {
    println(s"hello args! ${args.toSeq}")
    val sArgs = new ScallopArgs(args)

    sArgs.subcommand match {
      case Some(sArgs.datagen) => startDataGen(sArgs)
      case Some(sArgs.workload) => startWorkload(sArgs)
      case _ => new Exception(s"Unrecognized subcommand.\n${sArgs.printHelp()}")
    }

  }
}
