package com.ibm.sparktc.sparkbench.workload

case class WorkloadConfig (
  workload: String,
  inputDir: String,
  outputFormat: String,
  outputDir: String
)
