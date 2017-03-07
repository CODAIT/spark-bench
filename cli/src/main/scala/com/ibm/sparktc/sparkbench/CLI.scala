package com.ibm.sparktc.sparkbench

object CLI {

  def main(args: Array[String]): Unit = {
    println(s"hello args! ${args.toSeq}")
    val sArgs = new ScallopArgs(args)

    sArgs.subcommand match {
      case Some(sArgs.datagen) => ArgsParser.parseWorkload(sArgs) //how to get this conf to entry point?
      case Some(sArgs.workload) => {} //TODO
      case _ => new Exception(s"Unrecognized subcommand.\n${sArgs.printHelp()}")
    }
  }

}
