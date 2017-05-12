//package com.ibm.sparktc.sparkbench
//
//import com.ibm.sparktc.sparkbench.cli.CLIKickoff
//
//class ConfigFileTest extends BuildAndTeardownKMeansData {
//
//  override def beforeAll(): Unit = {
//    super.beforeAll()
//
//  }
//
//  "Spark-bench run through a config file" should "work" in {
//    val relativePath = "/etc/testConfFile1.conf"
//    val resource = getClass.getResource(relativePath)
//    val path = resource.getPath
//    CLIKickoff.main(Array(path))
//  }
//
//
//}
