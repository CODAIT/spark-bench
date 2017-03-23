package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.workload.mlworkloads.KMeansWorkload

object WorkloadKickoff {

  def apply(conf: WorkloadConfig): Unit = {
    conf.name.toLowerCase match {
      case "kmeans" => new KMeansWorkload(conf).run()
      case _ => new Exception(s"Unrecognized data generator name: ${conf.name}")
    }
  }

}