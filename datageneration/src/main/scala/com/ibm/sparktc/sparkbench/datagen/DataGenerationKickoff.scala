package com.ibm.sparktc.sparkbench.datagen

import com.ibm.sparktc.sparkbench.datagen.mlgenerator.{KmeansDataGen}

object DataGenerationKickoff {

  def apply(conf: DataGenerationConf): Unit = {
    conf.generatorName.toLowerCase match {
      case "kmeans" => new KmeansDataGen(conf).run
      case _ => new Exception(s"Unrecognized data generator name: ${conf.generatorName}")
    }
  }

}
