//package com.ibm.sparktc.sparkbench
//import com.ibm.sparktc.sparkbench.datagen.DataGenerationConf
//import org.scalatest.{FlatSpec, Matchers}
//
//class DataGenTest extends FlatSpec with Matchers {
//  "Cool" should "is a ridiculous test just to tie stuff together" in {
//    val x = DataGenerationConf(
//      generatorName = "kmeans",
//      numRows = 10,
//      outputFormat = "csv",
//      outputDir = "/tmp/spark-bench/",
//      generatorSpecific = Map.empty
//    )
//
//    println(x.numRows)
//
//    x.numRows shouldBe 10
//  }
//}