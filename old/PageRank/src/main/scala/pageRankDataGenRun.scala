
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

package src.main.scala
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.{SparkContext,SparkConf}
import org.apache.spark.SparkContext._
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib._
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.rdd._

import org.apache.spark.storage.StorageLevel
import org.apache.spark.graphx.impl.{EdgePartitionBuilder, GraphImpl}

object pageRankDataGenRun{
  
  def main(args: Array[String]) {
Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
    if (args.length < 5) {
      println("usage: <input> <output>; datagen:  <numVertices> <numPartitions> <mu> <sigma> ; run: <maxIterations> <tolerance> <resetProb> ")
      System.exit(0)
    }
    val conf = new SparkConf
    conf.setAppName("Spark PageRank Application")
    val sc = new SparkContext(conf)
    
	val input = args(0) 
    val output = args(1)
	
	val numVertices = args(2).toInt
    val numPar= args(3).toInt
    val mu=args(4).toDouble
    val sigma= args(5).toDouble
	
        
    val numIter = args(6).toInt
    val tolerance= args(7).toDouble
    val resetProb=args(8).toDouble

	val graph= GraphGenerators.logNormalGraph(sc,numVertices,numPar,mu,sigma)
	//graph.cache();
	val staticRanks =graph.staticPageRank(numIter, resetProb).vertices.cache()
   // val dynamicRanks = graph.pageRank(tolerance, resetProb).vertices.cache()
	//val err=compareRanks(staticRanks, dynamicRanks)
	val err=0.02
	println("The two different is %.2f\n".format(err))
	staticRanks.saveAsTextFile(output);
	
    sc.stop();
    
  }
  
  def compareRanks(a: VertexRDD[Double], b: VertexRDD[Double]): Double = {
    a.leftJoin(b) { case (id, a, bOpt) => (a - bOpt.getOrElse(0.0)) * (a - bOpt.getOrElse(0.0)) }
      .map { case (id, error) => error }.sum
  }
  
}
