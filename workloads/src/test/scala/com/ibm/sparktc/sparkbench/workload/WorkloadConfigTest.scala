//package com.ibm.sparktc.sparkbench.workload
//
//import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
//
//class WorkloadConfigTest extends FlatSpec with Matchers with BeforeAndAfterEach {
//
//  "WorkloadConfigs" should "get created from a Map" in {
//    val wcr = Suite(
//      name = "kmeans",
//      runs = 1,
//      parallel = false,
//      inputDir = Seq("/tmp/notathing"),
//      workloadResultsOutputDir = None,
//      outputDir = "/stuff",
//      workloadSpecific = Map(
//        "stuff" -> Seq(1)
//      )
//    )
//
//    val m = wcr.toMap()
//
////    val that = WorkloadConfig()
//
//
//  }
//
//}
