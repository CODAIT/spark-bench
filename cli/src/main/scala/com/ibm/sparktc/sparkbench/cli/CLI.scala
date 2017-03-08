package com.ibm.sparktc.sparkbench.cli

object CLI {

  def startWorkload(scallopArgs: ScallopArgs): Unit = {
    val conf = ArgsParser.parseWorkload(scallopArgs)
    println("OMFG I'M RUNNING A WORKLOAD")
  }

  def startDataGen(scallopArgs: ScallopArgs): Unit = {
    val conf = ArgsParser.parseDataGen(scallopArgs)
    println("OMFG I'M TOTES GENERATING DATA")
  }

  def main(args: Array[String]): Unit = {
    println(s"hello args! ${args.toSeq}")
    val sArgs = new ScallopArgs(args)

    val parsedArgs = sArgs.subcommand match {
      case Some(sArgs.datagen) => startWorkload(sArgs) //how to get this conf to entry point?
      case Some(sArgs.workload) => {} //TODO
      case _ => new Exception(s"Unrecognized subcommand.\n${sArgs.printHelp()}")
    }


  }

}
