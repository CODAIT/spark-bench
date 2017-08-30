package com.ibm.sparktc.sparkbench.sparklaunch

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

import com.ibm.sparktc.sparkbench.utils.TypesafeAccessories._
import com.typesafe.config._
import com.typesafe.config.ConfigRenderOptions.concise

import scala.collection.JavaConverters._
import scala.util.Try
import com.ibm.sparktc.sparkbench.sparklaunch.{SparkLaunchDefaults => SLD}


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

  /**
    * Takes in the path to the config file, splits that file to a bunch of files in a folder in /tmp, returns
    * the paths to those files.
    * @param path
    * @return a Seq of paths to the created files.
    */
  def apply(path: File): Seq[(SparkLaunchConf, String)] = {
    val config: Config = ConfigFactory.parseFile(path)
    val sparkBenchConfig = config.getObject(SLD.topLevelConfObject).toConfig
    val sparkContextConfs  = getListOfSparkSubmits(sparkBenchConfig)

    val processedConfs = sparkContextConfs.flatMap(processConfig)
    val confsAndPaths: Seq[(Config, String)] = processedConfs.map{ oneConf =>
      (oneConf, writePartialConfToDisk(sparkBenchConfig, oneConf))
    }

    val ret = confsAndPaths.map{ tuple => {
        val conf = tuple._1
        val tmpFilePath = tuple._2
        (SparkLaunchConf(conf, tmpFilePath), tmpFilePath)
      }
    }
    ret
  }

  /**
    * Run the confs through the transform pipeline
    * @param config
    * @return
    */
  private[sparklaunch] def processConfig(config: Config): Seq[Config] = {
    val a: SparkSubmitDeconstructedWithSeqs = SparkSubmitDeconstructedWithSeqs(config)
    if(a.sparkSubmitOptions.isEmpty) Seq(config)
    else {
      val b: Seq[SparkSubmitDeconstructed] = a.split()
      val c: Seq[SparkSubmitPieces] = b.map(_.splitPieces)
      val d: Seq[Config] = c.map(_.reconstruct)
      d
    }
  }


  /**
    * The one config file needs to be split up into individual files that can be submitted to spark-submit. This
    * @param rootConf
    * @param oneSparkSubmitConf
    * @return
    */
  private[sparklaunch] def writePartialConfToDisk(rootConf: Config, oneSparkSubmitConf: Config): String = {
    val tmpFile = Files.createTempFile("spark-bench-", ".conf")
    // Write this particular spark-context to a separate file on disk.
    val sbConf = stripSparkSubmitConfPaths(rootConf, oneSparkSubmitConf)
    val output = sbConf.root.render(concise.setJson(false))
    Files.write(tmpFile, output.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    tmpFile.toString
  }

  /**
    * Strip all config paths that are not essential for passing on to the next stage of spark-bench.
    * @param rootConf
    * @param oneSparkSubmitConf
    * @return
    */
  private[sparklaunch] def stripSparkSubmitConfPaths(rootConf: Config, oneSparkSubmitConf: Config): Config = {
    val stripped = oneSparkSubmitConf.withOnlyPath(SLD.suites)
    val suitesParallel: Boolean = Try(oneSparkSubmitConf.getBoolean(SLD.suitesParallel))
      .toOption
      .getOrElse(SLD.suitesParallelDefaultValue)
    val specifyParallelism = stripped.withValue(SLD.suitesParallel, ConfigValueFactory.fromAnyRef(suitesParallel))
    val wrapWithSparkSubmit = ConfigFactory.empty.withValue(
      SLD.sparkSubmitObject,
      ConfigValueFactory.fromIterable(Iterable(specifyParallelism.root).asJava)
    )
    val sbConf = ConfigFactory.empty.withValue(SLD.topLevelConfObject, wrapWithSparkSubmit.root)
    sbConf
  }

  /**
    * Utility method to simplify extracting the spark-submit configs
    * @param rootConfig
    * @return
    */
  private[sparklaunch] def getListOfSparkSubmits(rootConfig: Config): Seq[Config] = {
    getConfigListByName(s"${SLD.sparkSubmitObject}", rootConfig)
  }

}
