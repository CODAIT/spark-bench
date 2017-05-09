//package com.ibm.sparktc.sparkbench
//
//import com.holdenkarau.spark.testing.DataFrameSuiteBase
//import com.ibm.sparktc.sparkbench.cli.CLIKickoff
//import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
//import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.{stringifyStackTrace => SST}
//
//import scala.io.Source
//
//
///**
//  * Everything in the examples file that goes in the distribution should go through here to make sure it's up to date
//  */
//class ExamplesTest extends FlatSpec with Matchers with BeforeAndAfterAll with DataFrameSuiteBase {
//
//  override def beforeAll(): Unit ={
//    super.beforeAll()
//  }
//
//  override def afterAll(): Unit = {
//    super.afterAll()
//  }
//
//  def line2StrArray(line: String): Array[String] = {
//    line.split(" ")
//  }
//
//  def readFile(path: String): List[Array[String]] = {
//    val linesList = Source.fromFile(path).getLines().toList.filter(_.startsWith("bin/spark-bench.sh "))
//    linesList.map(line2StrArray)
//  }
//
//  "All the examples" should "work" in {
//    val lineArrays = readFile("kmeans-example.sh")
//    lineArrays.map( args => try {
//      println(s"${args.toSeq}")
//      CLIKickoff.main(args)
//    } catch {
//      case e: Exception => fail(s"This run of spark-bench failed: ${args.toSeq}\n ${SST(e)}")
//    }
//
//    )
//  }
//
//
//
//
//}
