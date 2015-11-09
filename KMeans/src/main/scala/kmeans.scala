import org.apache.spark.mllib.clustering.{KMeansModel, KMeans}
import org.apache.spark.{SparkContext,SparkConf}
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.linalg.Vectors

import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object KmeansApp {
  def main(args: Array[String]) {
Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
    if (args.length < 4) {
      println("usage: <input> <output> <numClusters> <maxIterations> <runs> - optional")
      System.exit(0)
    }
    val conf = new SparkConf
    conf.setAppName("Spark KMeans Example")
    val sc = new SparkContext(conf)
    
    val input = args(0)
    val output = args(1)
    val K = args(2).toInt
    val maxIterations = args(3).toInt
    val runs = calculateRuns(args)

    // Load and parse the data
    // val parsedData = sc.textFile(input)
    var start = System.currentTimeMillis();
    val data = sc.textFile(input)
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()
    val loadTime = (System.currentTimeMillis() - start).toDouble / 1000.0

    // Cluster the data into two classes using KMeans
    start = System.currentTimeMillis();
    val clusters: KMeansModel = KMeans.train(parsedData, K, maxIterations,runs, KMeans.K_MEANS_PARALLEL, seed=127L)
    val trainingTime = (System.currentTimeMillis() - start).toDouble / 1000.0
    println("cluster centers: " + clusters.clusterCenters.mkString(","))

    start = System.currentTimeMillis();
    val vectorsAndClusterIdx = parsedData.map{ point =>
      val prediction = clusters.predict(point)
      (point.toString, prediction)
    }
    vectorsAndClusterIdx.saveAsTextFile(output)
    val saveTime = (System.currentTimeMillis() - start).toDouble / 1000.0

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    start = System.currentTimeMillis();
    val WSSSE = clusters.computeCost(parsedData)
    val testTime = (System.currentTimeMillis() - start).toDouble / 1000.0

    println(compact(render(Map("loadTime" -> loadTime, "trainingTime" -> trainingTime, "testTime" -> testTime, "saveTime" -> saveTime))))
    println("Within Set Sum of Squared Errors = " + WSSSE)
    sc.stop()
  }
  def calculateRuns(args: Array[String]): Int = {
    if (args.length > 4) args(4).toInt
    else 1
  }
}
