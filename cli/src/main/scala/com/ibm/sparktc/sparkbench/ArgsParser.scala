package com.ibm.sparktc.sparkbench

object ArgsParser {
	def parseWorkload(sArgs: ScallopArgs): WorkloadConfig = {

    //TODO Replace this stub with actual validation and parsing of ScallopArgs to case class

    WorkloadConfig(
			workload = "KMeans",
			inputDir = "/tmp/spark-bench/input",
			outputFormat = "csv",
			outputDir = "/tmp/spark-bench/output"
		)
	}
}
