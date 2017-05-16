package com.ibm.sparktc.sparkbench.datageneration

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.{KMeansDataGen, LinearRegressionDataGen}
import org.apache.spark.sql.SparkSession
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow

object DataGenerationKickoff {
  val spark = createSparkContext()

  def createSparkContext(): SparkSession = {
    SparkSession
      .builder()
      .appName("spark-bench workload")
      .master(getOrThrow(sys.env.get("SPARK_MASTER_HOST")))
      .getOrCreate()
  }

  def apply(conf: DataGenerationConf): Unit = {
    conf.generatorName.toLowerCase match {
      case "kmeans" => new KMeansDataGen(conf, spark).run()
      case "linear-regression" => new LinearRegressionDataGen(conf, spark).run()
      case _ => throw new Exception(s"Unrecognized data generator name: ${conf.generatorName}")
    }
  }

}
