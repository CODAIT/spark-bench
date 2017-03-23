package com.ibm.sparktc.sparkbench.datageneration

case class DataGenerationConf (
                                generatorName: String,
                                numRows: Int,
                                numCols: Int,
                                outputDir: String,
                                outputFormat: String,
                                generatorSpecific: Map[String, Any]
                              )
