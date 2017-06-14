package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.{Files, Path}

import java.nio.file.Paths
import java.nio.file.StandardOpenOption

import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigRenderOptions}

import scala.collection.JavaConverters._
import scala.collection.immutable

/*
          _oo____oo_
         |          |
         |______(*)_|
         /\__----__/\
        /_/()||||()\_\
        |_\ o||||o /_|
        |----Jeep----|
        |_|        |_|
 */

object ConfigWrangler {

//  val uuid = java.util.UUID.randomUUID.toString

  /**
    * Takes in the path to the config file, splits that file to a bunch of files in a folder in /tmp, returns
    * the paths to those files.
    * @param path
    * @return a Seq of paths to the created files.
    */
  def apply(path: File): Seq[SparkLaunchConf] = {
    val config: Config = ConfigFactory.parseFile(path)
    val sparkBenchConfig = config.getObject("spark-bench").toConfig
    val sparkContextConfs = getConfigListByName("spark-contexts", sparkBenchConfig)

    val tmpDir = Files.createTempDirectory("spark-bench-tempfiles-")
    val tmpFiles: Seq[Path] = sparkContextConfs.indices.map(_ => Files.createTempFile(tmpDir, "spark-bench-", ".conf"))

    sparkContextConfs.indices.map( i =>
    {
      // Write this particular spark-context to a separate file on disk.
      val output = sparkContextConfs(i).root().render(ConfigRenderOptions.defaults())
      val str = s"spark-bench : {\nspark-contexts : [$output]\n}"
      Files.write(tmpFiles(i), str.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
      // Now create the SparkLaunchConfs
      SparkLaunchConf(sparkContextConfs(i), tmpFiles(i).toString)
    })
  }

  private def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList(name).asScala
    workloadObjs.map(_.toConfig).toList
  }



}
