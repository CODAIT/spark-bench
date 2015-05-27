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
      println("usage: <numPoints> <numClusters> <dimenstion> <scaling factor> <partition> <output>")
      System.exit(0)
    }
    val conf = new SparkConf
    conf.setAppName("Spark KMeans DataGen")
    val sc = new SparkContext(conf)
    
    
    
    
    val numPoint = args(0).toInt
    val numCluster= args(1).toInt
    val numDim=args(2).toInt
    val scaling= args(3).toDouble
    val numPar= args(4).toInt
    val output = args(5)
    
    
    val data= KMeansDataGenerator.generateKMeansRDD(sc,numPoint,numCluster,numDim,scaling,numPar)
    data.map(_.mkString(" ")).saveAsTextFile(output)
    
    sc.stop();
  }
  
}
