package com.ibm.sparktc.sparkbench.cli

import com.typesafe.config.ConfigException
import org.scalatest.{FlatSpec, Matchers}

class SetupTest extends FlatSpec with Matchers {
  "CLIKickoff" should "reject invalid argument strings" in {
    an [IllegalArgumentException] should be thrownBy CLIKickoff.main(Array())
    an [IllegalArgumentException] should be thrownBy CLIKickoff.main(Array("/etc/hostname", "and", "some", "extra", "arguments"))
    a [ConfigException] should be thrownBy CLIKickoff.main(Array("/dev/null/this/file/does/not/exist"))
  }
  "Configurator" should "parse config files properly" in {

  }
}
