package com.ibm.sparktc.sparkbench.sparklaunch

import com.typesafe.config.Config

import scala.util.Try

case class SparkLaunchConf(
                           master                 : String,
//                           deployMode             : String,
//                           executorMemory         : String,
//                           executorCores          : String,
//                           totalExecutorCores     : String,
//                           propertiesFile         : String,
//                           driverMemory           : String,
//                           driverCores            : String,
//                           driverExtraClassPath   : String,
//                           driverExtraLibraryPath : String,
//                           driverExtraJavaOptions : String,
//                           supervise              : String,
//                           queue                  : String,
//                           numExecutors           : String,
//                           files                  : String,
//                           pyFiles                : String,
//                           archives               : String,
                           `class`              : String,
//                           primaryResource        : String,
//                           name                   : String,
                           childArgs              : Array[String]
                           //,
//                           jars                   : String,
//                           packages               : String,
//                           packagesExclusions     : String,
//                           repositories           : String,
//                           verbose                : String
                           ){
  def toMap(cc: AnyRef): Map[String, Any] =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {(a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(cc))
    }

  def toSparkArgs(): Array[String] = {
    val mm = toMap(this).filterNot(keyValuePair => keyValuePair._1 == "childArgs")
    val m = mm.map(keyValue => keyValue._1 -> keyValue._2.asInstanceOf[String])
    val arr: Array[String] = m.flatMap(keyValuePair => Array(s"--${keyValuePair._1}", s"${keyValuePair._2}")).toArray
    val arrCurried = Array("--verbose") ++ arr ++ Array("cool") ++ childArgs
    arrCurried
  }
}
object SparkLaunchConf {

  def apply(sparkContextConf: Config, path: String): SparkLaunchConf =
    SparkLaunchConf(
      master = sparkContextConf.getString("master"),
      `class` = Try(sparkContextConf.getString("class")).toOption.getOrElse("com.ibm.sparktc.sparkbench.cli.CLIKickoff"),
      childArgs = Array(path)
    )





}


