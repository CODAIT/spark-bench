package com.ibm.sparktc.sparkbench.cli

import org.rogach.scallop._

class WorkloadArgs(name: String) extends Subcommand(name){
  val runs = opt[Int](short = 'n', required = false, default = Some(1), descr = "Number of times each workload variation is run")
  val parallel = opt[Boolean]("parallel", descr = "Specify this option to have the workloads run on the same SparkSession", noshort = true)
  val inputDir = opt[List[String]](short = 'i', required = true)
  val inputFormat = opt[String](required = false, default = Some("csv"))
  val outputDir = opt[String](short = 'o', required = true)
  val outputFormat = opt[String](short = 'f', default = Some("csv"))
  val workloadResultsOutputDir = opt[String](noshort = true, required = false, default = None)
  val workloadResultsOutputFormat = opt[String](noshort = true, required = false, default = None)

  def parseWorkloadArgs(): WorkloadConfBase = {
    WorkloadConfBase(
      runs.apply(),
      parallel.apply(),
      inputDir.apply(),
      workloadResultsOutputDir.toOption,
      outputDir.apply()
    )
  }
}

case class WorkloadConfBase(
                             runs: Int,
                             parallel: Boolean,
                             inputDir: Seq[String],
                             workloadResultsOutputDir: Option[String],
                             outputDir: String
                           )
