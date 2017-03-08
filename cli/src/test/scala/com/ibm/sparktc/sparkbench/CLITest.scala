package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.CLI
import org.scalatest.{FlatSpec, Matchers}

class CLITest extends FlatSpec with Matchers {
  "Main" should "return unit" in {
    CLI.main(Array("generate-data")) should be
  }
}