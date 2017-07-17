package com.ibm.sparktc.sparkbench.workload

case class MultiSuiteRunConfig(
                                suitesParallel: Boolean,
                                suites: Seq[Suite]
                              )
