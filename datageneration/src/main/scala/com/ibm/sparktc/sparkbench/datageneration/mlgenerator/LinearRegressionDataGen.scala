/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ibm.sparktc.sparkbench.datageneration.mlgenerator

import org.apache.spark.mllib.util.LinearDataGenerator
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import com.ibm.sparktc.sparkbench.datageneration.{DataGenerationConf, DataGenerator}
import com.ibm.sparktc.sparkbench.utils.LinearRegressionDefaults
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrDefault

class LinearRegressionDataGen (conf: DataGenerationConf, spark: SparkSession) extends DataGenerator(conf, spark) {

  import LinearRegressionDefaults._

  val m = conf.generatorSpecific //convenience

//  val output: String = args(0)
  val nExamples: Int = conf.numRows
  val nFeatures: Int = conf.numCols
  val eps: Double = getOrDefault(m, "eps", EPS)
  val intercepts: Double = getOrDefault(m, "intercepts", INTERCEPTS)
  val numPar: Int = getOrDefault(m, "partitions", NUM_OF_PARTITIONS)


  override def generateData(spark: SparkSession): DataFrame = {

    val data: RDD[LabeledPoint] = LinearDataGenerator.generateLinearRDD(
      spark.sparkContext,
      nExamples,
      nFeatures,
      eps,
      numPar,
      intercepts
    )

    import spark.implicits._
    data.toDF //TODO you can't output this to CSV. Parquet is fine
  }
}
