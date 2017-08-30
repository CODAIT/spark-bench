package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.utils.TypesafeAccessories.splitGroupedConfigToIndividualConfigs

case class Suite(
                  description: Option[String],
                  repeat: Int = 1,
                  parallel: Boolean = false,
                  benchmarkOutput: String,
                  workloadConfigs: Seq[Map[String, Any]]

                )

object Suite {

  def build(confsFromArgs: Seq[Map[String, Seq[Any]]],
            description: Option[String],
            repeat: Int,
            parallel: Boolean,
            benchmarkOutput: String): Suite = {
    Suite(
      description,
      repeat,
      parallel,
      benchmarkOutput,
      confsFromArgs.flatMap( splitGroupedConfigToIndividualConfigs )
    )
  }

}
