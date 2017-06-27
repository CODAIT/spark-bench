//package com.ibm.sparktc.sparkbench.workload
//
//import java.io.File
//
//import com.holdenkarau.spark.testing.{DataFrameSuiteBase, SharedSparkContext, Utils}
//import com.ibm.sparktc.sparkbench.utils.KMeansDefaults
//import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
//import org.apache.spark.mllib.util.KMeansDataGenerator
//import org.apache.spark.rdd.RDD
//import org.apache.spark.sql.types.{DoubleType, StructField, StructType}
//import org.apache.spark.sql.{DataFrame, Row, SQLContext, SparkSession}
//import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
//
//import scala.io.Source
//
//class WorkloadKickoffTest extends FlatSpec with Matchers with BeforeAndAfterEach with DataFrameSuiteBase {
//
//  def generateData(output: String) = {
//    val data: RDD[Array[Double]] = KMeansDataGenerator.generateKMeansRDD(
//      sc,
//      5,
//      KMeansDefaults.NUM_OF_CLUSTERS,
//      5,
//      KMeansDefaults.SCALING,
//      1
//    )
//    val schemaString = data.first().indices.map(_.toString).mkString(" ")
//    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, DoubleType, nullable = false))
//    val schema = StructType(fields)
//    val rowRDD = data.map(arr => Row(arr: _*))
//    val sQLContext = new SQLContext(sc)
//    val df = sQLContext.createDataFrame(rowRDD, schema)
//
//    writeToDisk(output, df)
//  }
//
//  val fileName = s"/tmp/kmeans/${java.util.UUID.randomUUID.toString}.csv"
//  val outputDir = "/tmp/testabillion.csv"
//
//
//  var file: File = _
//  var outputFile: File = _
//
//  override def beforeEach() {
//    file = new File(fileName)
//    outputFile = new File(outputDir)
//    generateData(fileName)
//  }
//
//  override def afterEach() {
//    Utils.deleteRecursively(file)
//    Utils.deleteRecursively(outputFile)
//  }
//
//  "WorkloadKickoff" should "throw an error when it's a non-recognized workload name" in {
//    val conf = WorkloadConfig(
//      name = "not a legit name",
//      runs = 1,
//      parallel = false,
//      inputDir = "whatever",
//      workloadResultsOutputDir = None,
//      outputDir = "whatever",
//      workloadSpecific = Map.empty
//    )
//
//    an[Exception] shouldBe thrownBy(WorkloadKickoff.kickoff(conf))
//  }
//
//  it should "produce a sequence of dataframes from a serial run of runWorkloads()" in {
//
//    val confs = Seq(
//      WorkloadConfig(
//        name = "kmeans",
//        runs = 1,
//        parallel = false,
//        inputDir = fileName,
//        workloadResultsOutputDir = None,
//        outputDir = "whatever",
//        workloadSpecific = Map.empty
//      ),
//      WorkloadConfig(
//        name = "kmeans",
//        runs = 1,
//        parallel = false,
//        inputDir = fileName,
//        workloadResultsOutputDir = None,
//        outputDir = "whatever",
//        workloadSpecific = Map.empty
//      )
//    )
//
//    val seqDF = WorkloadKickoff.runWorkloads(1, confs, false)
//
//    seqDF.length shouldBe 2
//  }
//
//  it should "produce a sequence of dataframes from a parallel run of runWorkloads()" in {
//    val confs = Seq(
//      WorkloadConfig(
//        name = "kmeans",
//        runs = 1,
//        parallel = true,
//        inputDir = fileName,
//        workloadResultsOutputDir = None,
//        outputDir = "whatever",
//        workloadSpecific = Map.empty
//      ),
//      WorkloadConfig(
//        name = "kmeans",
//        runs = 1,
//        parallel = true,
//        inputDir = fileName,
//        workloadResultsOutputDir = None,
//        outputDir = "whatever",
//        workloadSpecific = Map.empty
//      )
//    )
//
//    val seqDF = WorkloadKickoff.runWorkloads(1, confs, true)
//
//    seqDF.length shouldBe 2
//  }
//
//  it should "be able to produce one single DF from the sequence of DFs" in {
//    val confs = Seq(
//      WorkloadConfig(
//        name = "kmeans",
//        runs = 1,
//        parallel = true,
//        inputDir = fileName,
//        workloadResultsOutputDir = None,
//        outputDir = "whatever",
//        workloadSpecific = Map.empty
//      ),
//      WorkloadConfig(
//        name = "kmeans",
//        runs = 1,
//        parallel = true,
//        inputDir = fileName,
//        workloadResultsOutputDir = None,
//        outputDir = "whatever",
//        workloadSpecific = Map.empty
//      )
//    )
//
//    val seqDF = WorkloadKickoff.runWorkloads(1, confs, true)
//
//    seqDF.length shouldBe 2
//
//    val oneDF = WorkloadKickoff.joinDataFrames(seqDF)
//    val rowArray = oneDF.rdd.collect()
//
//    rowArray.length shouldBe 2
//
//    rowArray(0) shouldBe seqDF.head.first()
//    rowArray(1) shouldBe seqDF(1).first()
//
//  }
//
//  it should "save results to disk" in {
//
//
//    val confRoot = Suite(
//      name = "kmeans",
//      runs = 1,
//      parallel = true,
//      inputDir = Seq(fileName),
//      workloadResultsOutputDir = None,
//      outputDir = outputDir,
//      workloadSpecific = Map("k" -> Seq(1, 2))
//    )
//
//    val seqDF = WorkloadKickoff(confRoot)
//
//    val outFile = new File(outputDir)
//
//    outFile.exists() shouldBe true
//
//    val fileList = outFile.listFiles().toList.filter(_.getName.startsWith("part"))
//
//    fileList.length shouldBe 1
//
//    val fileContents: List[String] = fileList
//      .flatMap(
//        Source.fromFile(_)
//          .getLines()
//          .toList
//      )
//
//    val length: Int = fileContents.length
//
//    length shouldBe 2 + 1 // the +1 is for the header line
//
//    Utils.deleteRecursively(outFile)
//  }
//
//
//}
