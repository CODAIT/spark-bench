package com.ibm.sparktc.sparkbench.cli

import java.io.File

import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.workload.MultipleSuiteKickoff

object CLIKickoff extends App {

  override def main(args: Array[String]): Unit = {
    args.length match {
      case 1 => {
        val file = new File(args.head)
        if(!file.exists()) throw SparkBenchException(s"Cannot find configuration file: ${file.getPath}")
        val worksuites = Configurator(new File(args.head))
        MultipleSuiteKickoff.run(worksuites)
      }
      case _ => throw new IllegalArgumentException("Requires exactly one option: config file path")
    }
  }
}
