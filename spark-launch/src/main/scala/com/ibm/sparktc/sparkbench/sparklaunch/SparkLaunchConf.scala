package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File

import com.typesafe.config.Config
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow

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

  val thisJar = ClassLoader.getSystemClassLoader.getResource(".").getPath

  if(thisJar == null) println("AAAAAHHHH I DIDN'T FIND A JAR WHAT DO")
  else println(s"THIS IS MY JAR AREN'T YOU PROUD: $thisJar")

  def toSparkArgs(): Array[String] = {
    val mm = toMap(this).filterNot(keyValuePair => keyValuePair._1 == "childArgs")
    val m = mm.map(keyValue => keyValue._1 -> keyValue._2.asInstanceOf[String])
    val arr: Array[String] = m.flatMap(keyValuePair => Array(s"--${keyValuePair._1}", s"${keyValuePair._2}")).toArray
    val arrCurried = Array("--verbose") ++ arr ++ Array(thisJar) ++ childArgs
    arrCurried
  }
}
object SparkLaunchConf {


  def apply(sparkContextConf: Config, path: String): SparkLaunchConf =
    SparkLaunchConf(
      master = Try(sparkContextConf.getString("master")).toOption.getOrElse(sys.env("SPARK_MASTER_HOST")),
      `class` = Try(sparkContextConf.getString("class")).toOption.getOrElse("com.ibm.sparktc.sparkbench.cli.CLIKickoff"),
      childArgs = Array(path)
    )





}


