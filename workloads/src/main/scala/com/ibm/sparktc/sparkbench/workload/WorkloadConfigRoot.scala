package com.ibm.sparktc.sparkbench.workload

case class WorkloadConfigRoot (
                                name: String,
                                runs: Int,
                                parallel: Boolean,
                                inputDir: Seq[String],
//                                inputFormat: String,
//                                workloadResultsOutputFormat: Option[String],
                                workloadResultsOutputDir: Option[String],
                                outputDir: String,
//                                outputFormat: String,
                                workloadSpecific: Map[String, Any]
                              ){
//  def unapply(arg: WorkloadConfigRoot): Option[(Seq[String], Int, Boolean, Seq[String], String, Option[String], Option[String], String, String, Map[String, )] = {
//
//  }

  def unapply(): Map[String, Any] = {
    Map(
      "name" -> name,
      "runs" -> Seq(runs), // should always be a Sequence of size 1,
      // just putting it in a sequence for convenience in Cartesian product
      "inputDir" -> inputDir,
      "workloadResultsOutputDir" -> workloadResultsOutputDir,
      "outputDir" -> outputDir
    ) ++ workloadSpecific
  }

  def split(): Seq[WorkloadConfig] = {




    Seq()
  }

}
