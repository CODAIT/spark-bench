
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
import org.apache.spark.{SparkContext,SparkConf}
import org.apache.spark.SparkContext._
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib._
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.rdd._
import org.apache.log4j.Logger
import org.apache.log4j.Level

object pageRankDataGen{
  
  def main(args: Array[String]) {
    if (args.length < 5) {
      println("usage:  <output> <numVertices> <numPartitions> <mu> <sigma>")
      System.exit(0)
    }
		Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)   
	  val conf = new SparkConf
    conf.setAppName("Spark PageRankDataGen")
   
	 
    val sc = new SparkContext(conf)
    
        
    val output = args(0)
    val numVertices = args(1).toInt
    val numPar= args(2).toInt
    val mu=args(3).toDouble
    val sigma= args(4).toDouble
            
    
	val graph= GraphGenerators.logNormalGraph(sc,numVertices,numPar,mu,sigma)
	
	graph.edges.map(s=> s.srcId.toString+" "+s.dstId.toString+" "+s.attr.toString).saveAsTextFile(output)
    
    sc.stop();
  }
}