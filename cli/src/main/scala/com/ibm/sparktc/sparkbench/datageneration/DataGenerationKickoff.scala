package com.ibm.sparktc.sparkbench.datageneration

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.{KMeansDataGen}
import org.apache.spark.sql.SparkSession
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow

object DataGenerationKickoff {

  private def createSparkContext(): SparkSession = SparkSession.builder.getOrCreate

  val spark = createSparkContext()

//  def apply(mapSeq: Seq[Map[String, Any]]): Unit = {
  //
  //    mapSeq.map(m => DataGenConfigCreator(m))
  //
  //    conf.generatorName.toLowerCase match {
  //      case "kmeans" => new KMeansDataGen(conf, spark).run()
  //      case "linear-regression" => new LinearRegressionDataGen(conf, spark).run()
  //      case _ => throw new Exception(s"Unrecognized data generator name: ${conf.generatorName}")
  //    }
  //  }

    def apply(dgSeq: Seq[DataGenerator]): Unit = {
      dgSeq.foreach(_.run(spark))
    }
}
