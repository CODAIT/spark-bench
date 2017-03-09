package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.datagen.DataGenerationConf
import com.ibm.sparktc.sparkbench.workload.WorkloadConfig

object ArgsParser {
  def parseDataGen(sArgs: ScallopArgs): DataGenerationConf = {

		//TODO [DATAGEN] Replace this stub with actual validation and parsing of ScallopArgs to case class

		DataGenerationConf(
			generatorName = "kmeans",
			numRows = 10,
			outputFormat = "csv",
			outputDir = "/tmp/spark-bench/",
			generatorSpecific = Map.empty
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
