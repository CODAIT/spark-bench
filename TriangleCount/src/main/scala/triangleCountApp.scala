
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src.main.scala
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.{SparkContext,SparkConf, Logging}
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
