package com.ibm.sparktc.sparkbench.datageneration

import org.apache.spark.sql.{DataFrame, SparkSession}

abstract class DataGenerator(conf: DataGenerationConf, sparkSessOpt: Option[SparkSession] = None) {

  def createSparkContext(): SparkSession = {
    SparkSession
      .builder()
      .master("local[2]") //TODO this is a temp bandange, make this configurable along with all the other Spark stuff that needs to be configurable
      .appName("spark-bench generate-data")
      .getOrCreate()
  }

  def generateData(spark: SparkSession): DataFrame

  def writeToDisk(data: DataFrame): Unit = {
    conf.outputFormat match {
      case "csv" => data.write.csv(conf.outputDir)
      case _ => new Exception("unrecognized save format")
    }
  }

  def run(): Unit = {
    val spark = sparkSessOpt.getOrElse(createSparkContext())
    val data = generateData(spark)
    writeToDisk(data)
  }


//  import scala.collection.parallel.ForkJoinTaskSupport
//  val iterations = 10
//  val numConcurrentQueries = 5
//
//  val testInput = (1 to iterations).toList.map(number => number * 1000000)
//  val testInputPar = testInput.par
//  testInputPar.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(numConcurrentQueries))
//  testInputPar.map{ params => spark.sparkContext.parallelize(1 to params).count() }

}


