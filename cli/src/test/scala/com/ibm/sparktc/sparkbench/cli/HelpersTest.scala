package com.ibm.sparktc.sparkbench.cli

import com.ibm.sparktc.sparkbench.workload.Suite
import com.typesafe.config.ConfigException
import org.scalatest.{FlatSpec, Matchers}

class HelpersTest extends FlatSpec with Matchers {
  "CLIKickoff" should "reject invalid argument strings" in {
    an [IllegalArgumentException] should be thrownBy CLIKickoff.main(Array())
    an [IllegalArgumentException] should be thrownBy CLIKickoff.main(Array("/etc/hostname", "and", "some", "extra", "arguments"))
    a [ConfigException] should be thrownBy CLIKickoff.main(Array("/dev/null/this/file/does/not/exist"))
  }
  "Suite" should "split workload configs properly" in {
    val conf = Seq(
      Map("a" -> Seq(1, 2, 3), "b" -> Seq(4, 5)),
      Map("z" -> Seq(10, 20, 30))
    )
    val res = Seq(
      Map("a" -> 1, "b" -> 4),
      Map("a" -> 1, "b" -> 5),
      Map("a" -> 2, "b" -> 4),
      Map("a" -> 2, "b" -> 5),
      Map("a" -> 3, "b" -> 4),
      Map("a" -> 3, "b" -> 5),
      Map("z" -> 10),
      Map("z" -> 20),
      Map("z" -> 30)
    )
    val suite = Suite.build(conf, Some("description"), 1, false, "output")
    suite.description shouldBe Some("description")
    suite.repeat shouldBe 1
    suite.parallel shouldBe false
    suite.benchmarkOutput shouldBe "output"
    suite.workloadConfigs shouldBe res
  }
}
