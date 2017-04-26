package com.ibm.sparktc.sparkbench.cli

import com.typesafe.config.{Config, ConfigFactory}


object Configurator {
  def apply(path: String) = {
    val config: Config = ConfigFactory.load(path)

  }
}
