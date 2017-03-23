
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


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

object LabelPropagationApp {

    def main(args: Array[String]) {
Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
    if (args.length < 3) {
      println("usage: <input> <output>; datagen: <numVertices>;")
      System.exit(0)
    }
    val conf = new SparkConf
    conf.setAppName("Spark LabelPropagation Application")
    val sc = new SparkContext(conf)
    
	val input = args(0) 
    val output = args(1)
	val numVertices = args(2).toInt
    val numPar=args(3).toInt 
	
    val n=numVertices
    val clique1 = for (u <- 0L until n; v <- 0L until n) yield Edge(u, v, 1)
    val clique2 = for (u <- 0L to n; v <- 0L to n) yield Edge(u + n, v + n, 1)
    val twoCliques = sc.parallelize(clique1 ++ clique2 :+ Edge(0L, n, 1),numPar)
    val graph = Graph.fromEdges(twoCliques, 1)
      // Run label propagation
    val labels = LabelPropagation.run(graph, 20).cache()

      // All vertices within a clique should have the same label
    val clique1Labels = labels.vertices.filter(_._1 < n).map(_._2).collect.toArray
    val b1=clique1Labels.forall(_ == clique1Labels(0))
    val clique2Labels = labels.vertices.filter(_._1 >= n).map(_._2).collect.toArray
    val b2=clique2Labels.forall(_ == clique2Labels(0))
	val b3=clique1Labels(0) != clique2Labels(0)
	//println("result %d %d %d".format(b1,b2,b3));
    sc.stop();    
  }
  
}
