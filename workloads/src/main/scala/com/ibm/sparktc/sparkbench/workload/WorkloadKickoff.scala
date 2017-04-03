package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.workload.mlworkloads.KMeansWorkload
import scala.collection.parallel.ForkJoinTaskSupport

object WorkloadKickoff {

  def apply(conf: WorkloadConfigRoot): Unit = {
    val splitOutConfigs: Seq[WorkloadConfig] = conf.split()

    if(conf.parallel) {
        val confSeqPar = splitOutConfigs.par
        confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(splitOutConfigs.size))
        confSeqPar.map{ kickoff }
    }
    else {
      for (i <- splitOutConfigs.indices) {
        kickoff(splitOutConfigs(i))
      }
    }
  }

  def kickoff(conf: WorkloadConfig): Unit = conf.name.toLowerCase match {
    case "kmeans" => new KMeansWorkload(conf).run()
    case _ => new Exception(s"Unrecognized data generator name: ${conf.name}")
  }

}
