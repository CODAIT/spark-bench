package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.datageneration.{DataGenerationConf, DataGenerationKickoff}
import com.ibm.sparktc.sparkbench.workload.SuiteKickoff

object CLIKickoff extends App {

  def startWorkload(scallopArgs: ScallopArgs): Unit = {
    val suite = ArgsParser.parseWorkload(scallopArgs)
    println("OMFG I'M RUNNING A WORKLOAD")
    SuiteKickoff.run(suite)
  }

  def startDataGen(scallopArgs: ScallopArgs): Unit = {
    val conf: Map[String, Any] = ArgsParser.parseDataGen(scallopArgs)
    println("OMFG I'M TOTES GENERATING DATA")
    DataGenerationKickoff(DataGenerationConf(conf))
  }

  def useConfFile(sArgs: ScallopArgs): Unit = {
    println(sArgs.confFile.apply())
  }

  override def main(args: Array[String]): Unit = {
//    println(s"hello args! ${args.toSeq}")
    val sArgs = new ScallopArgs(args)

    sArgs.subcommand match {
      case Some(sArgs.datagen) => startDataGen(sArgs)
      case Some(sArgs.workload) => startWorkload(sArgs)
      case None => useConfFile(sArgs)
      case _ => new Exception(s"Unrecognized subcommand.\n${sArgs.printHelp()}")
    }

  }
}
