package com.ibm.sparktc.sparkbench

import com.ibm.sparktc.sparkbench.utils.test.UnitSpec
import com.typesafe.config.{ConfigFactory, ConfigValue}
import scala.collection.JavaConverters._

class ConfiguratorTest extends UnitSpec {

  "Configurator" should "test of importing a list in a generic way" in {
    val resource = getClass.getResource("/etc/ListTest.conf")
      val path = resource.getPath
    val conf = ConfigFactory.load(path)

    val stuff: Set[java.util.Map.Entry[String, ConfigValue]] = conf.entrySet().asScala.toSet
    val configValueMap = stuff.map(utilMapEntry => { utilMapEntry.getKey -> utilMapEntry.getValue }).toMap

    val test = configValueMap.head._2

    val vt = test.valueType()
  }
}
