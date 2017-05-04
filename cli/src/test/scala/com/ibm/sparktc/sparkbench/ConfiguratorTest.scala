package com.ibm.sparktc.sparkbench

import java.io.File
import java.util

import com.ibm.sparktc.sparkbench.utils.test.UnitSpec
import com.typesafe.config.{ConfigFactory, ConfigValue}

import scala.collection.JavaConverters._

class ConfiguratorTest extends UnitSpec {

  "Configurator" should "test of importing a list in a generic way" in {
    val resource = getClass.getResource("/etc/ListTest.conf")
      val path = resource.getPath
    val conf = ConfigFactory.parseFile(new File(path)).root()

    val unwrapped: Map[String, Any] = conf.unwrapped().asScala.toMap
  }
}
