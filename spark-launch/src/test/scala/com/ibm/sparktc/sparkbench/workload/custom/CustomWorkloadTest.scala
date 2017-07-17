package com.ibm.sparktc.sparkbench.workload.custom

import com.ibm.sparktc.sparkbench.sparklaunch.SparkLaunch
import org.scalatest.{FlatSpec, Matchers}

class CustomWorkloadTest extends FlatSpec with Matchers {
  "ConfigCreator" should "load and dispatch custom workloads from external JARs" in {
    val conf = getClass.getResource("/etc/custom-workload.conf").getPath
    SparkLaunch.main(Array(conf))
  }
}
