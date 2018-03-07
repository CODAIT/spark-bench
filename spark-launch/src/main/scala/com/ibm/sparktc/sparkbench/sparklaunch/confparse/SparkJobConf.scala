/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

package com.ibm.sparktc.sparkbench.sparklaunch.confparse

import java.io.File

import com.ibm.sparktc.sparkbench.sparklaunch.{SparkLaunchDefaults => SLD}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.utils.SparkBenchException
import com.typesafe.config.{Config, ConfigObject}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * The SparkJobConf case class is a neutral end-stage for the typesafe config file.
  * SparkJobConf can be neatly marshalled into submission-specific forms for launch.
  * For example, SparkJobConf can be marshalled into a LivyRequest case class for
  * submission through Livy, or to a similar form appropriate for submitting through
  * /bin/spark-submit.
  * @param className: String
  * @param sparkBenchJar: String
  * @param sparkConfs: Map[String, String]
  * @param sparkArgs: Map[String, String]
  * @param childArgs: Seq[String]
  * @param submissionParams: Map[String, Any]
  */
case class SparkJobConf(
                         className: String,
                         sparkBenchJar: String,
                         sparkConfs: Map[String, String],
                         sparkArgs: Map[String, String],
                         childArgs: Seq[String],
                         submissionParams: Map[String, Any]
                       )

object SparkJobConf {
  val log: Logger = LoggerFactory.getLogger(this.getClass)

  def apply(sparkContextConf: Config, childArg: String): SparkJobConf = {
    SparkJobConf(
      className = getSparkBenchClass(sparkContextConf),
      sparkBenchJar = getSparkBenchJar(sparkContextConf),
      sparkConfs = getSparkConfs(sparkContextConf),
      sparkArgs = getSparkArgs(sparkContextConf),
      childArgs = Seq(childArg),
      submissionParams =  getSubmissionParams(sparkContextConf)
    )
  }

  def getSubmissionParams(sparkContextConf: Config): Map[String, Any] =
    Try{
      val livyObj = sparkContextConf.getObject("livy")
      livyObj.unwrapped().asScala.toMap
    }.getOrElse{
      val sparkHome = Try(sparkContextConf.getString("spark-home")).getOrElse(
        getOrThrow[String](sys.env.get("SPARK_HOME"), "The environment variable SPARK_HOME must be set")
      )
      Map[String, Any]("spark-home" -> sparkHome)
    }

  def getSparkBenchClass(sparkContextConf: Config): String = {
    Try(sparkContextConf.getString("class")).getOrElse("com.ibm.sparktc.sparkbench.cli.CLIKickoff")
  }

  def getSparkArgs(sparkContextConf: Config): Map[String, String] = {
    val sparkConfMaps = Try(sparkContextConf.getObject("spark-args")).map(toStringMap).getOrElse(Map.empty)

    val master: Map[String, String] = {
      if (sparkConfMaps.contains("master")) Map.empty
      else if(sys.env.get("SPARK_MASTER_HOST").nonEmpty) {
        Map("master" -> sys.env("SPARK_MASTER_HOST"))
      }
      else Map.empty[String, String]
    }
    sparkConfMaps ++ master
  }

  def getSparkBenchJar(sparkContextConf: Config): String = {
    Try(sparkContextConf.getString(SLD.sparkBenchJar))
      .getOrElse(findSparkBenchJar(sparkContextConf))
  }

  /*
    If this condition is satisfied, then we're working from a distribution that has compiled jars.
    We're going to just dig around in the same parent dir as this jar to find the spark-bench jar
    because the user hasn't told us otherwise.
  */
  private def getDistributionJar(currentLocation: String): String = {
    val fileList: Seq[String] = new File(currentLocation).getParentFile.listFiles().toList.map(_.getPath)
    fileList.filterNot(_ == currentLocation).head
  }

  /*
    Assume here that we're in a testing environment. When `sbt test` runs for spark-launch, it'll
    first assemble the spark-bench jar and then copy it to spark-launch/test/resources/jars/spark-bench...jar.
    We reference that folder by just "/jars" because of relative paths.
  */
  private def findCompiledJarInRepo(): String = {
    val relativePath = "/jars"
    val path = Option(getClass.getResource(relativePath))
      .getOrElse(throw SparkBenchException("Failed to find compiled Spark-Bench jars"))
    val folder = new File(path.getPath)
    if(!folder.exists) throw SparkBenchException("Directory does not exist")
    if(!folder.isDirectory) throw SparkBenchException("Expected a directory but found a file")
    val filez = folder.listFiles.filterNot(file => file.getName.startsWith("spark-bench-launch"))
    filez.foreach(file => log.info(file.getName))
    val optFile: Option[File] = filez.find(file => file.getName.startsWith("spark-bench"))
    optFile match {
      case None => throw SparkBenchException("Failed to find Spark-Bench jar")
      case Some(f) => f.getPath
    }
  }

  def findSparkBenchJar(sparkContextConf: Config): String = {
    val whereIAm: String = this.getClass.getProtectionDomain.getCodeSource.getLocation.getFile

    if(whereIAm.endsWith(".jar")) {
      getDistributionJar(whereIAm)
    }
    else if(whereIAm.isEmpty) {
      throw SparkBenchException("Could not determine location for necessary spark-bench jars.")
    }
    else {
      findCompiledJarInRepo()
    }
  }

  def getSparkConfs(conf: Config): Map[String, String] = {
    Try(conf.getObject("conf")).map(toStringMap).getOrElse(Map.empty)
  }

  def toStringMap(co: ConfigObject): Map[String,String] =
    co.asScala.toMap.mapValues(v => v.unwrapped.toString)

}
