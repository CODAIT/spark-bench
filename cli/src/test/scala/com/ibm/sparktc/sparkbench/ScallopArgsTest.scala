package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.cli.ScallopArgs
import com.ibm.sparktc.sparkbench.utils.test.UnitSpec

class ScallopArgsTest extends UnitSpec with Capturing {

  val legitConfPath = getClass.getResource("/etc/testConfFile1.conf").getPath
  val badConfPath = "/not/a/legit/path.txt"

  "ScallopArgs" should "recognize the file path for the conf file" in {

    val sArgs = new ScallopArgs(
      Array(legitConfPath)
    )

    sArgs.confFile.toOption match {
      case None => fail()
      case _ =>
    }
  }

  it should "still work when you're trying to do just a straight workload or something" in {
    val sArgs = new ScallopArgs(
      Array("workload", "kmeans", "-i", "/tmp/coolstuff1", "/tmp/coolstuff2", "-o", "~/Desktop/test-results/",  "-k", "2", "32")
    )

    sArgs.confFile.toOption match {
      case Some(_) => fail()
      case _ =>
    }

    sArgs.subcommands match {
      case List(sArgs.workload, _) =>
      case _ => fail()
    }
  }

  it should "not have subcommands when there's only a filepath there" in {
    val sArgs = new ScallopArgs(
      Array(legitConfPath)
    )

    sArgs.subcommands match {
      case List(sArgs.workload, _) => fail()
      case _ =>
    }
  }

  it should "throw an error when the filepath is not legit" in {
    an [Exception] should be thrownBy { new ScallopArgs(Array(badConfPath)) }
  }

  it should "print the help message when asked" in {
    val (out, err, exits) = captureOutputAndExits(new ScallopArgs(Array("--help")))
    println(out)

    out should not be ""
  }
}
