package com.ibm.sparktc.sparkbench.workload

import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class WorkloadConfigRootTest  extends FlatSpec with Matchers with BeforeAndAfterEach {

  "WorkloadConfigRoot" should "got into a map cleanly" in {
    val wcr = WorkloadConfigRoot(
      name = "kmeans",
      runs = 1,
      parallel = false,
      inputDir = Seq("/tmp/notathing"),
      workloadResultsOutputDir = None,
      outputDir = "/stuff",
      workloadSpecific = Map(
        "stuff" -> Seq(1, 2, 3)
      )
    )

    val that = Map(
      "name" -> Seq("kmeans"),
      "runs" -> Seq(1),
      "inputDir" -> Seq("/tmp/notathing"),
      "workloadResultsOutputDir" -> Seq(None),
      "outputDir" -> Seq("/stuff"),
      "stuff" -> Seq(1, 2, 3)
    )

    wcr.toMap shouldBe that
  }

  it should "crossJoin a Seq of Seqs" in {
    val wcr = WorkloadConfigRoot(
      name = "kmeans",
      runs = 1,
      parallel = false,
      inputDir = Seq("/tmp/notathing"),
      workloadResultsOutputDir = None,
      outputDir = "/stuff",
      workloadSpecific = Map(
        "stuff" -> Seq(1, 2, 3)
      )
    )

    val list = Seq(Seq("kmeans"), Seq(1), Seq("/stuff"), Seq(1, 2, 3), Seq(None), Seq("/tmp/notathing"))

    val that = Seq(
      Seq("kmeans"), Seq(1), Seq("/stuff"), Seq(1), Seq(None), Seq("/tmp/notathing"),
      Seq("kmeans"), Seq(1), Seq("/stuff"), Seq(2), Seq(None), Seq("/tmp/notathing"),
      Seq("kmeans"), Seq(1), Seq("/stuff"), Seq(3), Seq(None), Seq("/tmp/notathing")
    )

    wcr.crossJoin(list)
  }

  it should "split into a Seq of WorkloadConfigs with all the combos" in {
    val wcr = WorkloadConfigRoot(
      name = "kmeans",
      runs = 1,
      parallel = false,
      inputDir = Seq("/tmp/notathing"),
      workloadResultsOutputDir = None,
      outputDir = "/stuff",
      workloadSpecific = Map(
        "stuff" -> Seq(1, 2, 3)
      )
    )

    val that = Seq(
      WorkloadConfig(
        name = "kmeans",
        runs = 1,
        parallel = false,
        inputDir = "/tmp/notathing",
        workloadResultsOutputDir = None,
        outputDir = "/stuff",
        Map(
          "stuff" -> 1
        )
      ),
      WorkloadConfig(
        name = "kmeans",
        runs = 1,
        parallel = false,
        inputDir = "/tmp/notathing",
        workloadResultsOutputDir = None,
        outputDir = "/stuff",
        Map(
          "stuff" -> 2
        )
      ),
      WorkloadConfig(
        name = "kmeans",
        runs = 1,
        parallel = false,
        inputDir = "/tmp/notathing",
        workloadResultsOutputDir = None,
        outputDir = "/stuff",
        Map(
          "stuff" -> 3
        )
      )
    )

    wcr.split() shouldBe that
  }

}
