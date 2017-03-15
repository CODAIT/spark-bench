package com.ibm.sparktc.sparkbench.workload

case class WorkloadConfig (
  workload: String,
  inputDir: String,
  inputFormat: String,
  workloadResultsOutputFormat: String,
  workloadResultsOutputDir: String,
  outputDir: String,
  outputFormat: String,
  workloadSpecific: Map[String, Any]
)
