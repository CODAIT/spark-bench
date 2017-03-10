package com.ibm.sparktc.sparkbench.workload

case class WorkloadConfig (
  workload: String,
  inputDir: String,
  inputFormat: String,
  outputFormat: String,
  outputDir: String
)
