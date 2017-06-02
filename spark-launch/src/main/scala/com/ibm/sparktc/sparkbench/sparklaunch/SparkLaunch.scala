package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File
import scala.sys.process._
import scala.collection.parallel.ForkJoinTaskSupport
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow

object SparkLaunch extends App {

  override def main(args: Array[String]): Unit = {
    println("lol")
    println(s"these are my argz: ${args.mkString(", ")}")
    assert(args.nonEmpty)
    val path = args.head
    val confSeq = SubmitConfigurator(new File(path))

    val sparkHome = getOrThrow(sys.env.get("SPARK_HOME"))

    val confSeqPar = confSeq.par
    //TODO address the concern that this could be confSeqPar.size threads for EACH member of ParSeq
    confSeqPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(confSeqPar.size))
    //TODO nope, this needs to launch new JVMs instead of just calling the method in a new thread
    confSeqPar.foreach( conf =>  {
      val argz: Array[String] = conf.toSparkArgs()
      println(s"argz are: ${argz.mkString(", ")}")
      val returnCode: Int = s"""$sparkHome/bin/spark-submit ${argz.mkString(" ")}""".!
      if (returnCode != 0) throw new Exception(s"spark-submit failed to complete properly given these arguments: \n\t${args.mkString(" ")}")
    })

  }
}
