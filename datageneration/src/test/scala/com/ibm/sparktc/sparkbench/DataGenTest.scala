package com.ibm.sparktc.sparkbench
import com.ibm.sparktc.sparkbench.datagen.DataGenerationConf
import org.scalatest.{FlatSpec, Matchers}

class DataGenTest extends FlatSpec with Matchers {
  "This" should "is a ridiculous test just to tie stuff together" in {
    val x = DataGenerationConf(
      numRows = 2,
      outputDir = "cool"
    )

    x.numRows shouldBe 2
  }
}