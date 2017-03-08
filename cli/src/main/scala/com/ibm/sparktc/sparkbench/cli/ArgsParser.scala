package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.datagen.DataGenerationConf
import com.ibm.sparktc.sparkbench.workload.WorkloadConfig

object ArgsParser {
  def parseDataGen(sArgs: ScallopArgs): DataGenerationConf = {

		//TODO [DATAGEN] Replace this stub with actual validation and parsing of ScallopArgs to case class

		DataGenerationConf(
			numRows = 10,
			outputDir = "/tmp/spark-bench/"
		)
  }

  def parseWorkload(sArgs: ScallopArgs): WorkloadConfig = {

    //TODO [WORKLOAD] Replace this stub with actual validation and parsing of ScallopArgs to case class

    WorkloadConfig(
			workload = "KMeans",
			inputDir = "/tmp/spark-bench/input",
			outputFormat = "csv",
			outputDir = "/tmp/spark-bench/output"
		)
	}
}
