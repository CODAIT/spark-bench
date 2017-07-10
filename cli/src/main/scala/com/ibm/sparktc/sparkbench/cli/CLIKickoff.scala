package com.ibm.sparktc.sparkbench.cli

import java.io.File

import com.ibm.sparktc.sparkbench.datageneration.DataGenerationKickoff
import com.ibm.sparktc.sparkbench.workload.MultipleSuiteKickoff

object CLIKickoff extends App {

  override def main(args: Array[String]): Unit = {
    args.length match {
      // We do want this to be serial so that one suite finishes entirely and the next suite starts.
      case 1 => {
        val worksuites = Configurator(new File(args.head))
//        DataGenerationKickoff(datagens)
        MultipleSuiteKickoff.run(worksuites)
      }
      case _ => new Exception("Requires exactly one option: config file path")
    }
  }
}
