package com.ibm.sparktc.sparkbench

import java.io.File

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import com.holdenkarau.spark.testing.Utils
import com.ibm.sparktc.sparkbench.cli.CLIKickoff
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.{stringifyStackTrace => SST}

import scala.io.Source

/**
  * Everything in the examples file that goes in the distribution should go through here to make sure it's up to date
  */
class ExamplesTest extends FlatSpec with Matchers with BeforeAndAfterAll with DataFrameSuiteBase {

  val path = "kmeans-example.sh"
  val datagenOutput = """-o (\S*)""".r.unanchored
  val exampleFileLines = Source.fromFile(path).getLines().toList.filter(_.startsWith("bin/spark-bench.sh "))
  val dataGenerationLines = exampleFileLines.filter(_.startsWith("bin/spark-bench.sh generate-data"))
  val outputFiles: Seq[String]  = dataGenerationLines.map(str => str match {
    case datagenOutput(f) => f.toString
  })

  override def beforeAll(): Unit ={
    super.beforeAll()
    outputFiles.map( str => Utils.deleteRecursively(new File(str)))
  }

  override def afterAll(): Unit = {
    super.afterAll()
    outputFiles.map( str => Utils.deleteRecursively(new File(str)))
  }

  def line2StrArray(line: String): Array[String] = {
    line.split(" ")
  }

  "All the examples" should "work" in {
    exampleFileLines.map(_.split(" ")).map( args => try {
      CLIKickoff.main(args)
    } catch {
      case e: Exception => fail(s"This run of spark-bench failed: ${args.toSeq}\n ${SST(e)}")
    })
  }




}
