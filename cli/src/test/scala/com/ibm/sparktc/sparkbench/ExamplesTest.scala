//package com.ibm.sparktc.sparkbench
//
//import java.io.File
//
//import com.holdenkarau.spark.testing.{DataFrameSuiteBase, LocalSparkContext, Utils}
//import com.ibm.sparktc.sparkbench.cli.CLIKickoff
//import com.ibm.sparktc.sparkbench.testfixtures.BuildAndTeardownData
//import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
//import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.{stringifyStackTrace => SST}
//
//import scala.io.Source
//
///**
//  * Everything in the examples file that goes in the distribution should go through here to make sure it's up to date
//  */
//class ExamplesTest extends FlatSpec with Matchers with BeforeAndAfterAll {
//
//  val path = "kmeans-example.sh"
//  val datagenOutput = """-o (\S*)""".r.unanchored
//  val exampleFileLines: Seq[String]  = Source.fromFile(path).getLines().toList.filter(_.startsWith("bin/spark-bench.sh "))
//  val dataGenerationLines = exampleFileLines.filter(_.startsWith("bin/spark-bench.sh generate-data"))
//  val outputFiles: Seq[String]  = dataGenerationLines.map(str => str match {
//    case datagenOutput(f) => f.toString
//  })
//
//  override def beforeAll(): Unit = {
//    BuildAndTeardownData.deleteFilesStr(outputFiles)
//    BuildAndTeardownData.deleteFolders()
//  }
//
//  override def afterAll(): Unit = {
//    BuildAndTeardownData.deleteFolders(outputFiles)
//    BuildAndTeardownData.deleteFolders()
//  }
//
//  def line2StrArray(line: String): Array[String] = {
//    line.split(" ")
//  }
//
//  "All the examples" should "work" in {
//    val realArgs = exampleFileLines.map(_.split(" ")).map(_.tail)
//    realArgs.map(args => try {
//      println(args.mkString(" "))
//      CLIKickoff.main(args)
//    } catch {
//      case e: Exception => fail(s"This run of spark-bench failed: ${args.toSeq}\n ${SST(e)}")
//    })
//  }
//
//
//
//
//}
