package com.ibm.sparktc.sparkbench.sparklaunch

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
      confsFromArgs.flatMap( splitToIndividualWorkloadConfigs )
    )
  }

  //http://stackoverflow.com/questions/14740199/cross-product-in-scala
  private def crossJoin(list: Seq[Seq[Any]]): Seq[Seq[Any]] =
    list match {
      case xs :: Nil => xs map (Seq(_))
      case x :: xs => for {
        i <- x
        j <- crossJoin(xs)
      } yield Seq(i) ++ j
    }

  private def splitToIndividualWorkloadConfigs(m: Map[String, Seq[Any]]): Seq[Map[String, Any]] = {
    val keys: Seq[String] = m.keys.toSeq
    val valuesSeq: Seq[Seq[Any]] = m.map(_._2).toSeq //for some reason .values wasn't working properly

    // All this could be one-lined, I've multi-lined it and explicit typed it for clarity
    val joined: Seq[Seq[Any]] = crossJoin(valuesSeq)
    val zipped: Seq[Seq[(String, Any)]] = joined.map(keys.zip(_))
    val mapSeq: Seq[Map[String, Any]] = zipped.map(_.map(kv => kv._1.toLowerCase -> kv._2).toMap)

    mapSeq
  }
}
