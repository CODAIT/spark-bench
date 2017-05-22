package com.ibm.sparktc.sparkbench.workload.ml

case class MLWorkloadResults(
                              workloadName: String,
                              loadTime: Long,
                              trainTime: Long,
                              testTime: Long,
                              saveTime: Long
                            )
