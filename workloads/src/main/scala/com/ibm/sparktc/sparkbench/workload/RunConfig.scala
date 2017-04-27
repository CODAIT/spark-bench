package com.ibm.sparktc.sparkbench.workload

import scala.annotation.tailrec

case class RunConfig(
                                name: String,
                                runs: Int,
                                parallel: Boolean,
                                inputDir: Seq[String],
                                workloadResultsOutputDir: Option[String],
                                outputDir: String,
                                workloadSpecific: Map[String, Seq[Any]]
                              ) {

  def toMap() = {
    Map(
      "name" -> Seq(name),
      "runs" -> Seq(runs), // should always be a Sequence of size 1, just putting it in a sequence for convenience in Cartesian product
      "inputDir" -> inputDir,
      "workloadResultsOutputDir" -> Seq(workloadResultsOutputDir),
      "outputDir" -> Seq(outputDir)
    ) ++ workloadSpecific
  }


  //http://stackoverflow.com/questions/14740199/cross-product-in-scala
  def crossJoin(list: Seq[Seq[Any]]): Seq[Seq[Any]] =
    list match {
      case xs :: Nil => xs map (Seq(_))
      case x :: xs => for {
        i <- x
        j <- crossJoin(xs)
      } yield Seq(i) ++ j
    }

  def splitToWorkloads(): Seq[WorkloadConfig] = {
    val m = toMap()
    val keys: Seq[String] = m.keys.toSeq
    val valuesSeq: Seq[Seq[Any]] = m.map(_._2).toSeq
//    println(valuesSeq)
    //this could be one-lined, I've multi-lined it and explicit typed it for clarity
    val joined: Seq[Seq[Any]] = crossJoin(valuesSeq)
    val zipped: Seq[Seq[(String, Any)]] = joined.map(keys.zip(_))
    val mapSeq: Seq[Map[String, Any]] = zipped.map(_.map(kv => kv._1.toLowerCase -> kv._2).toMap)
    val confs: Seq[WorkloadConfig] = mapSeq.map(WorkloadConfig.fromMap)

    confs
  }
}
