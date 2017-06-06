package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File

import com.typesafe.config.{Config, ConfigObject}
import scala.collection.JavaConverters._

import scala.util.Try

case class SparkLaunchConf(
                            `class`: String,
                            sparkBenchJar: String,
                            sparkConfs : Array[String],
                            sparkArgs: Array[String],
                            childArgs: Array[String]
                          ){

  def toSparkArgs: Array[String] =
    Array(s"--class ${`class`}") ++ sparkArgs ++ sparkConfs ++ Array(sparkBenchJar) ++ childArgs

}

object SparkLaunchConf {

  def apply(sparkContextConf: Config, path: String): SparkLaunchConf = {
    SparkLaunchConf(
      `class` = getSparkBenchClass(sparkContextConf),
      sparkBenchJar = getSparkBenchJar(sparkContextConf),
      sparkArgs = getSparkArgs(sparkContextConf),
      sparkConfs = getSparkConfs(sparkContextConf),
      childArgs = Array(path)
    )
  }

  def getSparkBenchClass(sparkContextConf: Config): String =
    Try(sparkContextConf.getString("class")).toOption.getOrElse("com.ibm.sparktc.sparkbench.cli.CLIKickoff")

  def getSparkArgs(sparkContextConf: Config): Array[String] = {
    val sparkConfMaps = Try(sparkContextConf.getObject("spark-args")).map(toStringMap).getOrElse(Map.empty)
    sparkConfMaps.foldLeft(Array[String]()) { case (arr, (k, v)) => arr ++ Array(s"--$k $v") }
  }

  def getSparkBenchJar(sparkContextConf: Config): String =
    Try(sparkContextConf.getString("class")).toOption match {
      case Some(str) => str
      case _ => {
        val thisJar = ClassLoader.getSystemClassLoader.getResource(".")
        // If thisJar == null or if the path doesn't end with a .jar extension,
        // then assume we're doing sbt test
        if (thisJar == null || !thisJar.getPath.endsWith(".jar")) {
          val relativePath = "/jars"
          val path = getClass.getResource(relativePath)
          val folder = new File(path.getPath)
          assert(folder.exists && folder.isDirectory)
          val filez = folder.listFiles.toList
          filez.foreach(file => println(file.getName))
          filez.filter(file => file.getName.startsWith("spark-bench")).head.getPath
        }
        // Else assume we're in a compiled jar
        else {
          val path = thisJar.getPath
          println(s"THIS IS MY JAR AREN'T YOU PROUD: $path")
          val fileList: Seq[String] = new File(path).getParentFile.listFiles().toList.map(_.getPath)
          fileList.filterNot(_ == thisJar).head
        }
      }
  }

  def getSparkConfs(conf: Config): Array[String] = {
    val sparkConfMaps = Try(conf.getObject("conf")).map(toStringMap).getOrElse(Map.empty)
    sparkConfMaps.foldLeft(Array[String]()) { case (arr, (k, v)) => arr ++ Array(s"--conf $k=$v") }
  }


  def toStringMap(co: ConfigObject): Map[String,String] =
    co.asScala.toMap.mapValues(v => v.unwrapped.toString)


}


