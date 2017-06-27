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
    val numRows = getOrThrow(map, "numrows").asInstanceOf[Int]
    val numCols = getOrThrow(map, "numcols").asInstanceOf[Int]
    val outputDir = getOrThrow(map, "outputdir").asInstanceOf[String]
    val outputFormat = getOrDefault[Option[String]](map, "outputformat", None)

    val keys = map.keySet
    val workloadSpecificKeys = keys.diff(commonKeys)
    val generatorSpecific = workloadSpecificKeys.map(key => {key -> map.get(key).get}).toMap

    DataGenerationConf(
      name,
      numRows,
      numCols,
      outputDir,
      outputFormat,
      generatorSpecific
    )
  }
}
