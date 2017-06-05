package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File

import com.ibm.sparktc.sparkbench.sparklaunch.SubmitConfigurator.toStringMap
import com.typesafe.config.Config
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow

import scala.util.Try

case class SparkLaunchConf(
                            `class`              : String,
                            sparkBenchJar        : String,
                          sparkConfs : Array[String],
                            childArgs             : Array[String]
                           ){
//  def toMap(cc: AnyRef): Map[String, Any] =
//    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {(a, f) =>
//      f.setAccessible(true)
//      a + (f.getName -> f.get(cc))
//    }

  def toSparkArgs: Array[String] = {
    Array(s"--class ${`class`}") ++ sparkConfs ++ Array(sparkBenchJar) ++ childArgs
  }
}
object SparkLaunchConf {


  def apply(sparkContextConf: Config, path: String): SparkLaunchConf =
    SparkLaunchConf(
      `class` = Try(sparkContextConf.getString("class")).toOption.getOrElse("com.ibm.sparktc.sparkbench.cli.CLIKickoff"),
      sparkBenchJar = getSparkBenchJar,
      sparkConfs = Array("cool"),
      childArgs = Array(path) //TODO accommodate the CLI
    )

  def getSparkBenchJar: String = {
    val thisJar = ClassLoader.getSystemClassLoader.getResource(".").getPath
    // If thisJar == null then assume we're doing sbt test
    if(thisJar == null) {
      val relativePath = "/jars"
      val resource = getClass.getResource(relativePath)
      val path = getClass.getResource(relativePath)
      val folder = new File(path.getPath)
      assert (folder.exists && folder.isDirectory)
      val filez = folder.listFiles.toList
      filez.foreach(file => println(file.getName))
      filez.filter(file => file.getName.startsWith("spark-bench")).head.getPath
    }
    // Else assume we're in a compiled jar
    else {
      println(s"THIS IS MY JAR AREN'T YOU PROUD: $thisJar")
      val fileList: Seq[String]  = new File(thisJar).getParentFile.listFiles().toList.map(_.getPath)
      fileList.filterNot(_ == thisJar).head
    }
  }

  def getSparkConfs(conf: Config): Array[String] = {
    val sparkConfMaps = Try(conf.getObject("conf")).map(toStringMap).getOrElse(Map.empty)
    sparkConfMaps.foldLeft(Array[String]()) { case (arr, (k, v)) => arr ++ Array(s"--conf $k=$v") }
  }





}


