package com.ibm.sparktc.sparkbench.cli

import org.rogach.scallop._


class DataGeneratorArgs(name: String) extends Subcommand(name){
  val numRows = opt[Int](short = 'r', required = true)
  val numCols = opt[Int](short = 'c', required = true)
  val outputDir = opt[String](short = 'o', required = true)
  val outputFormat = opt[String](short = 'f', default = None)

  def parseDataGeneratorArgs(): DataGeneratorConfBase = {
    DataGeneratorConfBase(
      numRows.apply(),
      numCols.apply(),
      outputDir.apply(),
      outputFormat.toOption
    )
  }
}

case class DataGeneratorConfBase(
                                  numRows: Int,
                                  numCols: Int,
                                  outputDir: String,
                                  outputFormat: Option[String]
                                )
