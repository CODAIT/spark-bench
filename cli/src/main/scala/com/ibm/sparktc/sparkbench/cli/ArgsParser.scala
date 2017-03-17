package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.datagen.DataGenerationConf
import com.ibm.sparktc.sparkbench.workload.WorkloadConfig
import scala.language.reflectiveCalls // Making SBT hush about the feature warnings


object ArgsParser {


	/*
    Conf.subcommands match {
      case List(Conf.sub1, Conf.sub1.sub1a) => something1()
      case List(Conf.sub1, Conf.sub1.sub1b) => something2()
    }
  */

	def parseDataGen(sArgs: ScallopArgs): DataGenerationConf = {

		val numRows = sArgs.datagen.numRows.apply()
		val numCols = sArgs.datagen.numCols.apply()
		val outputDir = sArgs.datagen.outputDir.apply()
		val outputFormat = sArgs.datagen.outputFormat.apply()

		// DATA GENERATION ARG PARSING, ONE FOR EACH GENERATOR
		val (name, map) = sArgs.subcommands match {
			// KMEANS
			case List(sArgs.datagen, sArgs.datagen.kmeans) => (
				"kmeans",
				Map(
					"k"	-> sArgs.datagen.kmeans.k.apply(),
					"scaling" -> sArgs.datagen.kmeans.scaling.apply(),
					"partitions" -> sArgs.datagen.kmeans.partitions.apply()
				)
			)
			// OTHER
			case _ => throw new Exception(s"Unknown or unimplemented generator: ${sArgs.datagen}")
		}

		// OUTPUT COLLECTED ARGUMENTS
		DataGenerationConf(
			name,
			numRows,
			numCols,
			outputDir = outputDir,
			outputFormat = outputFormat,
			map
		)
  }

  def parseWorkload(sArgs: ScallopArgs): WorkloadConfig = {

    //TODO [WORKLOAD] Replace this stub with actual validation and parsing of ScallopArgs to case class

    WorkloadConfig(
			workload = "KMeans",
			inputDir = "/tmp/spark-bench/input",
			inputFormat = "csv",
			workloadResultsOutputDir = "/tmp/spark-bench/workloadoutput",
			workloadResultsOutputFormat = "csv",
			outputDir = "/tmp/spark-bench/output",
			outputFormat = "csv",
			workloadSpecific = Map.empty
		)
	}

}
