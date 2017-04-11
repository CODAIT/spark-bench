package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.datageneration.DataGenerationConf
import com.ibm.sparktc.sparkbench.workload.WorkloadConfigRoot

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
		val outputFormat = sArgs.datagen.outputFormat.toOption

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

  def parseWorkload(sArgs: ScallopArgs): WorkloadConfigRoot = {

		val runs = sArgs.workload.runs.apply()
		val parallel = sArgs.workload.parallel.apply()
		val inputDir = sArgs.workload.inputDir.apply()
		val outputDir = sArgs.workload.outputDir.apply()
		val workloadResOut = sArgs.workload.workloadResultsOutputDir.toOption

		// Workload ARG PARSING, ONE FOR EACH workload
		val (name: String, map: Map[String, Seq[Any]]) = sArgs.subcommands match {
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

    WorkloadConfigRoot(
			name = name,
			runs = runs,
			parallel = parallel,
			inputDir = inputDir,
			workloadResultsOutputDir = workloadResOut,
			outputDir = outputDir,
			workloadSpecific = map
		)
	}

}
