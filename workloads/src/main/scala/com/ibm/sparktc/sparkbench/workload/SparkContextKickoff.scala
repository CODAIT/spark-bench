package com.ibm.sparktc.sparkbench.workload

object SparkContextKickoff {

  def run(seq: Seq[SparkContextConf]): Unit = {
    seq.map(contextConf => {
      val spark = contextConf.createSparkContext()
      contextConf.suites.map(suite => {
        SuiteKickoff.run(suite, spark)
      })
    })
  }

}
