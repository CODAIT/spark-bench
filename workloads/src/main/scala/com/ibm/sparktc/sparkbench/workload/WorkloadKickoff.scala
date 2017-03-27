package com.ibm.sparktc.sparkbench.workload

import com.ibm.sparktc.sparkbench.workload.mlworkloads.KMeansWorkload
import scala.collection.parallel.ForkJoinTaskSupport

object WorkloadKickoff {


  def apply(conf: WorkloadConfigRoot): Unit = {

    val confs: Seq[WorkloadConfig] = conf.split()

    //TODO if parallel == true, kick these off in threads
    conf.name.toLowerCase match {
      case "kmeans" => new KMeansWorkload(confs.head).run()
      case _ => new Exception(s"Unrecognized data generator name: ${conf.name}")
    }
  }



//  val iterations = 10
//  val numConcurrentQueries = 5
//
//  val confSeq: Seq[WorkloadConfig] = Seq(conf)
//  val confSeqPar = confSeq.par
//  confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(numConcurrentQueries))
//  confSeqPar.map{ wc =>  }

}
