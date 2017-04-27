package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.datageneration.DataGenerationConf
import com.ibm.sparktc.sparkbench.workload.RunConfig

import scala.language.reflectiveCalls // Making SBT hush about the feature warnings


object ArgsParser {


	/*
    Conf.subcommands match {
      case List(Conf.sub1, Conf.sub1.sub1a) => something1()
      case List(Conf.sub1, Conf.sub1.sub1b) => something2()
    }
  */

	def parseDataGen(sArgs: ScallopArgs): DataGenerationConf = {

		// DATA GENERATION ARG PARSING, ONE FOR EACH GENERATOR
		val (name, base, map) = sArgs.subcommands match {
			// KMEANS
			case List(sArgs.datagen, sArgs.datagen.kmeans) => (
				"kmeans",
				sArgs.datagen.kmeans.parseDataGeneratorArgs(),
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
			base.numRows,
			base.numCols,
			outputDir = base.outputDir,
			outputFormat = base.outputFormat,
			map
		)
  }

  def parseWorkload(sArgs: ScallopArgs): RunConfig = {

		// Workload ARG PARSING, ONE FOR EACH workload
		val (name: String, base: WorkloadConfBase, map: Map[String, Seq[Any]]) = sArgs.subcommands match {
			// KMEANS
			case List(sArgs.workload, sArgs.workload.kmeans) => (
				"kmeans",
				sArgs.workload.kmeans.parseWorkloadArgs(),
				Map(
					"k"	-> sArgs.workload.kmeans.k.apply(),
					"maxIterations" -> sArgs.workload.kmeans.maxIterations.apply(),
					"seed" -> sArgs.workload.kmeans.seed.apply()
				)
			)
			// OTHER
			case _ => throw new Exception(s"Unknown or unimplemented generator: ${sArgs.datagen}")
		}

    RunConfig(
			name = name,
			runs = base.runs,
			parallel = base.parallel,
			inputDir = base.inputDir,
			workloadResultsOutputDir = base.workloadResultsOutputDir,
			outputDir = base.outputDir,
			workloadSpecific = map
		)
	}

//	def parseConfFile(sArgs: ScallopArgs): RunConfig = {
//
//	}
}
