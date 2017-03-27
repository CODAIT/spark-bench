package com.ibm.sparktc.sparkbench.workload

case class WorkloadConfig (
                            name: String,
                            runs: Int,
                            parallel: Boolean,
                            inputDir: String,
                            workloadResultsOutputDir: Option[String],
                            outputDir: String,
                            workloadSpecific: Map[String, Any]
)
