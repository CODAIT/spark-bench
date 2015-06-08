/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kmeans_min.src.main.scala
import org.apache.spark.mllib.clustering.{KMeansModel,KMeans}
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.{SparkContext,SparkConf}


object KmeansDataGen {
  def main(args: Array[String]) {
    if (args.length < 5) {
      println("usage: <output> <numPoints> <numClusters> <dimenstion> <scaling factor> [numpar]")
      System.exit(0)
    }
    val conf = new SparkConf
    conf.setAppName("Spark KMeans DataGen")
    val sc = new SparkContext(conf)
    
    
    
    
    val output = args(0)
    val numPoint = args(1).toInt
    val numCluster= args(2).toInt
    val numDim=args(3).toInt
    val scaling= args(4).toDouble
    val defPar = if (System.getProperty("spark.default.parallelism") == null) 2 else System.getProperty("spark.default.parallelism").toInt
    val numPar = if (args.length > 5) args(5).toInt else defPar
    
    
    val data= KMeansDataGenerator.generateKMeansRDD(sc,numPoint,numCluster,numDim,scaling,numPar)
    data.map(_.mkString(" ")).saveAsTextFile(output)
    
    sc.stop();
  }
  
}
