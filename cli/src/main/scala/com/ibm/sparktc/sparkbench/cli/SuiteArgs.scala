package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.workload.Suite
import org.rogach.scallop._

class SuiteArgs(name: String*) extends Subcommand(name:_*) {
  //todo the dir and format arguments should have a codependent verification relationship. See the Scallop wiki for more.

  val runs = opt[Int](short = 'n', required = false, default = Some(1), descr = "Number of times each workload variation is run")
  val parallel = opt[Boolean]("parallel", descr = "Specify this option to have the workloads run on the same SparkSession", noshort = true)
  val inputFormat = opt[String](required = false, default = Some("csv"))
  val outputDir = opt[String](short = 'o', required = true)
  val outputFormat = opt[String](short = 'f', default = Some("csv"))
  val workloadResultsOutputDir = opt[String](noshort = true, required = false, default = None)
  val workloadResultsOutputFormat = opt[String](noshort = true, required = false, default = None)
//  val workloadResultsOutputNumbered = opt[Boolean](noshort = true, required = false, descr = "")
  val description = opt[String](required = false)

  val inputDir = opt[List[String]](short = 'i', required = true)

  val nameName: String = name.head
//  println(s"nameName is: $nameName")
//  println(s"nameName class is: ${nameName.getClass}")

  def parseWorkoadArgs()(workloadArgLists: Map[String, Seq[Any]]): Suite = {
    val workloadSpecificArgs: Map[String, Seq[Any]] = Map(
      "name" -> Seq(nameName),
      "input" -> inputDir.apply(),
      "workloadresultsoutputdir" -> Seq(workloadResultsOutputDir.toOption)
    ) ++ workloadArgLists

    Suite.build(
      Seq(workloadSpecificArgs),
      description.toOption,
      runs.apply(),
      parallel.apply(),
      outputDir.apply()
    )
  }
}

//case class WorkloadConfBase(
//                             runs: Int,
//                             parallel: Boolean,
//                             inputDir: Seq[String],
//                             workloadResultsOutputDir: Option[String],
//                             outputDir: String
//                           )
