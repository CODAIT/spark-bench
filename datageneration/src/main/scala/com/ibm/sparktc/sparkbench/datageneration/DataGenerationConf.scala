package com.ibm.sparktc.sparkbench.datageneration

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._

case class DataGenerationConf (
                                generatorName: String,
                                numRows: Int,
                                numCols: Int,
                                outputDir: String,
                                outputFormat: Option[String],
                                generatorSpecific: Map[String, Any]
                              )

object DataGenerationConf {

  val commonKeys = Set(
    "name",
    "numRows",
    "numCols",
    "outputDir"
  )

  def apply(map: Map[String, Any]): DataGenerationConf = {
    val name = getOrThrow(map, "name").asInstanceOf[String]
    val numRows = getOrThrow(map, "numRows").asInstanceOf[Int]
    val numCols = getOrThrow(map, "numCols").asInstanceOf[Int]
    val outputDir = getOrThrow(map, "outputDir").asInstanceOf[String]

    val keys = map.keySet
    val workloadSpecificKeys = keys.diff(commonKeys)
    val generatorSpecific = workloadSpecificKeys.map(key => {key -> map.get(key)}).toMap

    DataGenerationConf(
      name,
      numRows,
      numCols,
      outputDir,
      None,
      generatorSpecific
    )
  }
}
