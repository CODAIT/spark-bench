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

		val inputDir = sArgs.workload.inputDir.apply()
		val inputFormat = sArgs.workload.inputFormat.apply()
		val outputDir = sArgs.workload.outputDir.apply()
		val outputFormat = sArgs.workload.outputFormat.apply()
		val workloadResOut = sArgs.workload.workloadResultsOutputDir.toOption
		val workloadFormatOut = sArgs.workload.workloadResultsOutputFormat.toOption

		// Workload ARG PARSING, ONE FOR EACH workload
		val (name, map) = sArgs.subcommands match {
			// KMEANS
			case List(sArgs.workload, sArgs.workload.kmeans) => (
				"kmeans",
				Map(
					"k"	-> sArgs.workload.kmeans.k.apply(),
					"maxIterations" -> sArgs.workload.kmeans.maxIterations.apply(),
					"seed" -> sArgs.workload.kmeans.seed.apply()
				)
			)
			// OTHER
			case _ => throw new Exception(s"Unknown or unimplemented generator: ${sArgs.datagen}")
		}


    WorkloadConfig(
			name = name,
			inputDir = inputDir,
			inputFormat = inputFormat,
			workloadResultsOutputDir = workloadResOut,
			workloadResultsOutputFormat = workloadFormatOut,
			outputDir = outputDir,
			outputFormat = outputFormat,
			workloadSpecific = map
		)
	}

}
