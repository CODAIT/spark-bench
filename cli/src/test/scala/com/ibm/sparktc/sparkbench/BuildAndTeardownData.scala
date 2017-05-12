package com.ibm.sparktc.sparkbench

import java.io.File

import com.holdenkarau.spark.testing.{DataFrameSuiteBase, Utils}
import com.ibm.sparktc.sparkbench.datageneration.DataGenerationConf
import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.KMeansDataGen
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import org.apache.spark.sql.SparkSession

object BuildAndTeardownData {
  val inputFolder = "/tmp/spark-bench-test"
  val inputFile = s"$inputFolder/kmeans-data.parquet"
  val inputFileForExamples = "/tmp/spark-bench-demo"

  def deleteFiles(): Unit = {
    Utils.deleteRecursively(new File(inputFolder))
    Utils.deleteRecursively(new File(inputFileForExamples))
  }

  def deleteFiles(fileSeq: Seq[String]): Unit = {
    fileSeq.foreach(str => Utils.deleteRecursively(new File(str)))
  }

  def generateKMeansData(spark: SparkSession): Unit = {
    val conf = DataGenerationConf(
      generatorName = "kmeans",
      numRows = 100,
      numCols = 10,
      outputDir = inputFile,
      outputFormat = None,
      generatorSpecific = Map[String, Any]()
    )

    val kMeansDataGen = new KMeansDataGen(conf, spark)

    val df = kMeansDataGen.generateData(spark)

    writeToDisk(conf.outputDir, df, spark)
  }

}
