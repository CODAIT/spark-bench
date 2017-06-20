package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File

import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
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

  def getSparkBenchClass(sparkContextConf: Config): String = {
    Try(sparkContextConf.getString("class")).toOption.getOrElse("com.ibm.sparktc.sparkbench.cli.CLIKickoff")
  }

  def getSparkArgs(sparkContextConf: Config): Array[String] = {
    val sparkConfMaps = Try(sparkContextConf.getObject("spark-args")).map(toStringMap).getOrElse(Map.empty)

    val correctedSparkConf = {
      if(!sparkConfMaps.contains("master")) {
        val envMaster = getOrThrow(sys.env.get("SPARK_MASTER_HOST"))
        sparkConfMaps ++ Map("master" -> envMaster)
      }
      else sparkConfMaps
    }

    assert(correctedSparkConf.contains("master"))

    correctedSparkConf.foldLeft(Array[String]()) { case (arr, (k, v)) => arr ++ Array(s"--$k $v") }
  }

  def getSparkBenchJar(sparkContextConf: Config): String = {

    val whereIAm = this.getClass.getProtectionDomain.getCodeSource.getLocation.getFile
    println(s"I'M HERE::: $whereIAm")

    if(whereIAm.endsWith(".jar")) {
      /*
          If this condition is satisfied, then we're working from a distribution that has compiled jars.
          We're going to just dig around in the same parent dir as this jar to find the spark-bench jar
          because the user hasn't told us otherwise.
       */
      val fileList: Seq[String] = new File(whereIAm).getParentFile.listFiles().toList.map(_.getPath)
      fileList.filterNot(_ == whereIAm).head
    }
    else if(whereIAm.isEmpty) {
      throw new SparkBenchException("Could not determine location for necessary spark-bench jars."); null
    }
    else {
      /* Assume here that we're in a testing environment. When `sbt test` runs for spark-launch, it'll
         first assemble the spark-bench jar and then copy it to spark-launch/test/resources/jars/spark-bench...jar.
         We reference that folder by just "/jars" because of relative paths. */
      val relativePath = "/jars"
      val path = getClass.getResource(relativePath)
      assert(path != null)
      val folder = new File(path.getPath)
      assert(folder.exists && folder.isDirectory)
      val filez = folder.listFiles.toList.filterNot(file => file.getName.startsWith("spark-bench-launch"))
      filez.foreach(file => println(file.getName))
      filez.filter(file => file.getName.startsWith("spark-bench")).head.getPath
    }
  }

  def getSparkConfs(conf: Config): Array[String] = {
    val sparkConfMaps = Try(conf.getObject("conf")).map(toStringMap).getOrElse(Map.empty)
    sparkConfMaps.foldLeft(Array[String]()) { case (arr, (k, v)) => arr ++ Array(s"--conf $k=$v") }
  }


  def toStringMap(co: ConfigObject): Map[String,String] =
    co.asScala.toMap.mapValues(v => v.unwrapped.toString)


}


