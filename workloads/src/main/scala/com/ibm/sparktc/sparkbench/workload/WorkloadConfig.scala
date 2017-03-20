package com.ibm.sparktc.sparkbench.workload

case class WorkloadConfig (
                            name: String,
                            inputDir: String,
                            inputFormat: String,
                            workloadResultsOutputFormat: Option[String],
                            workloadResultsOutputDir: Option[String],
                            outputDir: String,
                            outputFormat: String,
                            workloadSpecific: Map[String, Any]
)
