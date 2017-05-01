package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.datageneration.DataGenerationConf
import com.ibm.sparktc.sparkbench.workload.Suite

import scala.language.reflectiveCalls // Making SBT hush about the feature warnings


object ArgsParser {


	/*
    Conf.subcommands match {
      case List(Conf.sub1, Conf.sub1.sub1a) => something1()
      case List(Conf.sub1, Conf.sub1.sub1b) => something2()
    }
  */

	def parseDataGen(sArgs: ScallopArgs): Map[String, Seq[Any]] = {

		// DATA GENERATION ARG PARSING, ONE FOR EACH GENERATOR
		val m: Map[String, Seq[Any]] = sArgs.subcommands match {
			// KMEANS
			case List(sArgs.datagen, sArgs.datagen.kmeans) =>
				"name" -> Seq(kmeans"),
				numRows.apply(),
				numCols.apply(),
				outputDir.apply(),
				outputFormat.toOption,
				Map(
					"k"	-> sArgs.datagen.kmeans.k.apply(),
					"scaling" -> sArgs.datagen.kmeans.scaling.apply(),
					"partitions" -> sArgs.datagen.kmeans.partitions.apply()
				)
			)
			// OTHER
			case _ => throw new Exception(s"Unknown or unimplemented generator: ${sArgs.datagen}")

		m
  }

  def parseWorkload(sArgs: ScallopArgs): Map[String, Seq[Any]] = {

		// Workload ARG PARSING, ONE FOR EACH workload
		val stuff = sArgs.subcommands match {
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

    Suite(
			name = name,
			runs = base.runs,
			parallel = base.parallel,
			inputDir = base.inputDir,
			outputDir = base.outputDir,

		)
	}

//	def parseConfFile(sArgs: ScallopArgs): RunConfig = {
//
//	}
}
