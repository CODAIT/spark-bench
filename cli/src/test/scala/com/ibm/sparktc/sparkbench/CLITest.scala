package com.ibm.sparktc.sparkbench

import org.scalatest.{FlatSpec, Matchers}

class CLITest extends FlatSpec with Matchers {
  "Main" should "return unit" in {
    CLI.main(Array("stuff")) shouldBe Unit
  }
}