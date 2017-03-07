
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
import org.apache.spark.storage.StorageLevel


object triangleCountApp {

  def main(args: Array[String]) {
    if (args.length != 4) {    
	  println("usage: <input> <output> <minEdge> <StorageLevel> ")
      System.exit(0)
    }
	Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
	
    val conf = new SparkConf
    conf.setAppName("Spark TriangleCount Application")
    val sc = new SparkContext(conf)
    
    val input = args(0) 
    val output = args(1)
    val minEdge = args(2).toInt
	val storageLevel=args(3)
    
	var sl:StorageLevel=StorageLevel.MEMORY_ONLY;
	if(storageLevel=="MEMORY_AND_DISK_SER")
		sl=StorageLevel.MEMORY_AND_DISK_SER
	else if(storageLevel=="MEMORY_AND_DISK")
		sl=StorageLevel.MEMORY_AND_DISK
	
    val graph = GraphLoader.edgeListFile(sc, input, true, minEdge, sl, sl).partitionBy(PartitionStrategy.RandomVertexCut)    

    val triCounts = graph.triangleCount().vertices        
    println("num triangles are "+triCounts.count());
    sc.stop();
    
  }
  
}
