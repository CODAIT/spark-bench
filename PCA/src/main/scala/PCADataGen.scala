/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PCA.src.main.scala
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.mllib.util.LinearDataGenerator
import org.apache.spark.{SparkContext,SparkConf}


object PCADataGen {
  def main(args: Array[String]) {
Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
    if (args.length < 3) {
      println("usage: <output> <numExamples> <numFeatures> [numpar]")
      System.exit(0)
    }
    val conf = new SparkConf
    conf.setAppName("Spark PCA DataGen")
    val sc = new SparkContext(conf)
    
    val output = args(0)
    val numExamples = args(1).toInt
    val numFeatures = args(2).toInt
    val epsilon = 100
    val defPar = if (System.getProperty("spark.default.parallelism") == null) 2 else System.getProperty("spark.default.parallelism").toInt
    val numPar = if (args.length > 3) args(3).toInt else defPar

    val data= LinearDataGenerator.generateLinearRDD(sc,numExamples,numFeatures,epsilon,numPar)
    data.saveAsTextFile(output)
    
    sc.stop();
  }
  
}
