package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

import com.typesafe.config._
import com.typesafe.config.ConfigRenderOptions.concise

import scala.collection.JavaConverters._

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
  def apply(path: File): Seq[(SparkLaunchConf, String)] = {
    val config: Config = ConfigFactory.parseFile(path)
    val sparkBenchConfig = config.getObject("spark-bench").toConfig
    val sparkContextConfs = getConfigListByName("spark-contexts", sparkBenchConfig)

    sparkContextConfs.map { conf =>
      val tmpFile = Files.createTempFile("spark-bench-", ".conf")
      // Write this particular spark-context to a separate file on disk.
      val newConf = sparkBenchConfig
        .withoutPath("spark-contexts")
        .withValue("spark-contexts", ConfigValueFactory.fromIterable(Iterable(conf.root).asJava))
      val sbConf = ConfigFactory.empty.withValue("spark-bench", newConf.root)
      val output = sbConf.root.render(concise.setJson(false))
      Files.write(tmpFile, output.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
      // Now create the SparkLaunchConfs
      (SparkLaunchConf(conf, tmpFile.toString), tmpFile.toString)
    }
  }

  private def getConfigListByName(name: String, config: Config): List[Config] = {
    val workloadObjs: Iterable[ConfigObject] = config.getObjectList(name).asScala
    workloadObjs.map(_.toConfig).toList
  }



}
