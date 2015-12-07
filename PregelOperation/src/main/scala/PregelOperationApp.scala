
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


object PregelOperationApp {
  
  def main(args: Array[String]) {
Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
    if (args.length < 3) {
      println("usage: <input> <output> <minEdge> ")
      System.exit(0)
    }
    val conf = new SparkConf
    conf.setAppName("Spark PregelOperation Application")
    val sc = new SparkContext(conf)
    
    val input = args(0) 
    val output = args(1)
    val minEdge = args(2).toInt 
   

    
    val loadedgraph = GraphLoader.edgeListFile(sc, input, true, minEdge, StorageLevel.MEMORY_AND_DISK, StorageLevel.MEMORY_AND_DISK).partitionBy(PartitionStrategy.RandomVertexCut)    
    
	val graph: Graph[Int, Double] =loadedgraph.mapEdges(e => e.attr.toDouble)
	//val graph: Graph[Int, Double] =
      //GraphGenerators.logNormalGraph(sc,numVertices,numPar,mu,sigma).mapEdges(e => e.attr.toDouble)
    
	val sourceId: VertexId = 42 // The ultimate source
    // Initialize the graph such that all vertices except the root have distance infinity.
    val initialGraph = graph.mapVertices((id, _) => if (id == sourceId) 0.0 else Double.PositiveInfinity)
    val sssp = initialGraph.pregel(Double.PositiveInfinity)(
      (id, dist, newDist) => math.min(dist, newDist), // Vertex Program
      triplet => {  // Send Message
        if (triplet.srcAttr + triplet.attr < triplet.dstAttr) {
          Iterator((triplet.dstId, triplet.srcAttr + triplet.attr))
        } else {
          Iterator.empty
        }
      },
      (a,b) => math.min(a,b) // Merge Message
    )
    //println(sssp.vertices.collect.mkString("\n"))
    println("vertices count: "+sssp.vertices.count())
    //res.saveAsTextFile(output);
    
    sc.stop();
    
  }
  
}
