package com.ibm.sparktc.sparkbench.datagen

case class DataGenerationConf (
                                generatorName: String,
                                numRows: Int,
                                outputDir: String,
                                outputFormat: String,
                                generatorSpecific: Map[String, Any]
                              )
