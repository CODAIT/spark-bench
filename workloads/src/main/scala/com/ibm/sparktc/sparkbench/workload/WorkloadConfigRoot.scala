package com.ibm.sparktc.sparkbench.workload

case class WorkloadConfigRoot (
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
      "runs" -> Seq(runs), // should always be a Sequence of size 1,
      // just putting it in a sequence for convenience in Cartesian product
      "inputDir" -> Seq(inputDir),
      "workloadResultsOutputDir" -> Seq(workloadResultsOutputDir),
      "outputDir" -> Seq(outputDir)
    ) ++ workloadSpecific
  }


  //http://stackoverflow.com/questions/14740199/cross-product-in-scala
  def crossJoin[T](list: Seq[Seq[T]]): Seq[Seq[T]] =
    list match {
      case xs :: Nil => xs map (Seq(_))
      case x :: xs => for {
        i <- x
        j <- crossJoin(xs)
      } yield Seq(i) ++ j
    }

  def split(): Seq[WorkloadConfig] = {
    val m = toMap()
    val keys: Seq[String] = m.keys.toSeq
    val valuesSeq = m.values.toSeq
    val joined: Seq[Seq[Any]] = crossJoin(valuesSeq)
    val zipped: Seq[Map[String, Any]] = joined.map(keys.zip(_).map(kv => kv._1 -> kv._2).toMap)
    val confs: Seq[WorkloadConfig] = zipped.map(WorkloadConfig.fromMap)

    confs
  }
}
