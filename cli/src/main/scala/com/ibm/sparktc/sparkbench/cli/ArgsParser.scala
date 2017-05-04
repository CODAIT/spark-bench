package com.ibm.sparktc.sparkbench.cli

import java.io.File

import com.ibm.sparktc.sparkbench.workload.{SparkContextConf, Suite}

import scala.language.reflectiveCalls // Making SBT hush about the feature warnings

object ArgsParser {

	def parseDataGen(sArgs: ScallopArgs): Map[String, Any] = {

		// DATA GENERATION ARG PARSING, ONE FOR EACH GENERATOR
		val m: Map[String, Any] = sArgs.subcommands match {
			// KMEANS
			case List(sArgs.datagen, sArgs.datagen.kmeans) => Map(
				"name" -> "kmeans",
				"numrows" -> sArgs.datagen.kmeans.numRows.apply(),
				"numcols" -> sArgs.datagen.kmeans.numCols.apply(),
				"outputdir" -> sArgs.datagen.kmeans.outputDir.apply(),
				"outputformat" -> sArgs.datagen.kmeans.outputFormat.toOption,
				"k" -> sArgs.datagen.kmeans.k.apply(),
				"scaling" -> sArgs.datagen.kmeans.scaling.apply(),
				"partitions" -> sArgs.datagen.kmeans.partitions.apply()
			)
			// OTHER
			case _ => throw new Exception(s"Unknown or unimplemented generator: ${sArgs.datagen}")
		}

		m
	}

  def parseWorkload(sArgs: ScallopArgs): SparkContextConf = {
		val subcommand: SuiteArgs = sArgs.workload.subcommand.get.asInstanceOf[SuiteArgs]
//		val parseWorkloadSpecificArgs = subcommand.parseWorkloadArgs()
		val master = sys.env.getOrElse("SPARK_MASTER_HOST", "")

		// Workload ARG PARSING, ONE FOR EACH workload
		val workloadArgs: Map[String, Seq[Any]] = sArgs.subcommands match {
			// KMEANS
			case List(sArgs.workload, sArgs.workload.kmeans) => Map (
					"name" -> "kmeans",
					"k"	-> sArgs.workload.kmeans.k.apply(),
					"maxIterations" -> sArgs.workload.kmeans.maxIterations.apply(),
					"seed" -> sArgs.workload.kmeans.seed.apply()
				)
			// TIMED SLEEP
			case List(sArgs.workload, sArgs.workload.timedsleep) => Map (
					"name" -> "kmeans",
					"partitions"	-> sArgs.workload.timedsleep.partitions.apply(),
					"sleepms" -> sArgs.workload.timedsleep.sleepMS.apply()
				)
			// OTHER
			case _ => throw new Exception(s"Unknown or unimplemented generator: ${sArgs.datagen}")
		}

		val suite = subcommand.parseWorkloadArgs()(workloadArgs)

		SparkContextConf(
			suites = Seq(suite),
			master = master
		)
	}

	def parseConfFile(sArgs: ScallopArgs): Seq[SparkContextConf] = {
		val path: File = sArgs.confFile.apply()
		Configurator(path)
	}
}
