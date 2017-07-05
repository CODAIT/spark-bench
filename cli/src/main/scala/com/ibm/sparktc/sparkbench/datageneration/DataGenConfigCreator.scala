package com.ibm.sparktc.sparkbench.datageneration

import com.ibm.sparktc.sparkbench.datageneration.mlgenerator.{KMeansDataGen, LinearRegressionDataGen}
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._

object DataGenConfigCreator {

  def apply(m: Map[String, Any]): DataGenerator = {
    val name = getOrThrow(m, "name").asInstanceOf[String].toLowerCase
    name match {
      case "kmeans" => new KMeansDataGen(m)
      case "linearregression" => new LinearRegressionDataGen(m)
      case _ => throw SparkBenchException(s"Unrecognized or implemented workload name: $name")
    }
  }

}