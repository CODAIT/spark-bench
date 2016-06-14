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

package kmeans_min.src.main.scala

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.{SparkConf, SparkContext}

object KmeansDataGen {
  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
    if (args.length < 5) {
      println("usage: <output> <numPoints> <numClusters> <dimenstion> <scaling factor> [numpar]")
      System.exit(0)
    }
    val conf = new SparkConf
    conf.setAppName("Spark KMeans DataGen")
    val sc = new SparkContext(conf)

    val output = args(0)
    val numPoint = args(1).toInt
    val numCluster = args(2).toInt
    val numDim = args(3).toInt
    val scaling = args(4).toDouble
    val defPar = if (System.getProperty("spark.default.parallelism") == null) 2 else System.getProperty("spark.default.parallelism").toInt
    val numPar = if (args.length > 5) args(5).toInt else defPar

    val data = KMeansDataGenerator.generateKMeansRDD(sc, numPoint, numCluster, numDim, scaling, numPar)
    data.map(_.mkString(" ")).saveAsTextFile(output)

    sc.stop();
  }

}
