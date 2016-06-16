/*
 * (C) Copyright IBM Corp. 2015 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
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
    val clusters: KMeansModel = KMeans.train(parsedData, K, maxIterations, runs, KMeans.K_MEANS_PARALLEL, seed = 127L)
    val trainingTime = (System.currentTimeMillis() - start).toDouble / 1000.0
    println("cluster centers: " + clusters.clusterCenters.mkString(","))

    start = System.currentTimeMillis();
    val vectorsAndClusterIdx = parsedData.map { point =>
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
