import org.apache.spark.mllib.clustering.{KMeansModel, KMeans}
import org.apache.spark.{SparkContext,SparkConf}
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.linalg.Vectors

object KmeansApp {
  def main(args: Array[String]) {
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
    val data = sc.textFile(input)
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()
    // Cluster the data into two classes using KMeans
    
    val clusters: KMeansModel = KMeans.train(parsedData, K, maxIterations,runs)
     println("cluster centers: " + clusters.clusterCenters.mkString(","))
     
    val vectorsAndClusterIdx = parsedData.map{ point =>
      val prediction = clusters.predict(point)
      (point.toString, prediction)
    }

    vectorsAndClusterIdx.saveAsTextFile(output)
    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(parsedData)
    println("Within Set Sum of Squared Errors = " + WSSSE)
    sc.stop()
  }
  def calculateRuns(args: Array[String]): Int = {
    if (args.length > 4) args(4).toInt
    else 1
  }
}
