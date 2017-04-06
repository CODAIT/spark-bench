package com.ibm.sparktc.sparkbench.datageneration

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.{KMeansDataGen}

object DataGenerationKickoff {

  def apply(conf: DataGenerationConf): Unit = {
    conf.generatorName.toLowerCase match {
      case "kmeans" => new KMeansDataGen(conf).run
      case _ => throw new Exception(s"Unrecognized data generator name: ${conf.generatorName}")
    }
  }

}
