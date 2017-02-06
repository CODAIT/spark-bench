
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


object SVDPlusPlusApp {
  
  def main(args: Array[String]) {
    if (args.length !=12) {
      println("usage: <input> <output> <minEdge> <numIter> <rank> <minVal> <maxVal> <gamma1> <gamma2> <gamma6> <gamma7> <StroageLevel>")
      System.exit(0)
    }
  	Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val sconf = new SparkConf
    sconf.setAppName("Spark SVDPlusPlus Application")
    val sc = new SparkContext(sconf)
    
    val input = args(0) 
    val output = args(1)
    val minEdge= args(2).toInt
    val numIter = args(3).toInt
	val rank=args(4).toInt
	val minVal=args(5).toDouble
	val maxVal=args(6).toDouble
	val gamma1=args(7).toDouble
	val gamma2=args(8).toDouble
	val gamma6=args(9).toDouble
	val gamma7=args(10).toDouble
  
  val storageLevel=args(11)
  
  
    
    
  var sl:StorageLevel=StorageLevel.MEMORY_ONLY;
  if(storageLevel=="MEMORY_AND_DISK_SER")
    sl=StorageLevel.MEMORY_AND_DISK_SER
  else if(storageLevel=="MEMORY_AND_DISK")
    sl=StorageLevel.MEMORY_AND_DISK
    
   var conf = new SVDPlusPlus.Conf(rank, numIter, minVal, maxVal, gamma1, gamma2, gamma6, gamma7)
  
  var edges:  org.apache.spark.rdd.RDD[org.apache.spark.graphx.Edge[Double]]= null
  val dataset="small";
  if(dataset=="small"){
     val graph = GraphLoader.edgeListFile(sc, input, true, minEdge,sl, sl).partitionBy(PartitionStrategy.RandomVertexCut)
       edges=graph.edges.map{ e => {
        var attr=0.0
        if(e.dstId %2 ==1) attr=5.0 else attr=1.0 
          Edge(e.srcId,e.dstId,e.attr.toDouble) 
        } 
    }
   edges.persist()
  }  else if(dataset=="large"){
      edges = sc.textFile(input).map { line =>
      val fields = line.split("::")
      Edge(fields(0).toLong , fields(1).toLong, fields(2).toDouble)
      
    }
    edges.persist()    
    
  }else{
    sc.stop()
    System.exit(1)
    
  }
    
    var (newgraph, u) = SVDPlusPlus.run(edges, conf)
    newgraph.persist()
    
	var tri_size=newgraph.triplets.count() //collect().size
	
    var err = newgraph.vertices.collect().map{ case (vid, vd) =>
        if (vid % 2 == 1) vd._4 else 0.0
    }.reduce(_ + _) / tri_size
    
        
    println("the err is %.2f".format(err))
	
	//second iteration
	/*conf.rank=20
	conf.maxIters=10
	
    var (newgraph, u) = SVDPlusPlus.run(edges, conf)
    newgraph.persist()
    
	var tri_size=newgraph.triplets.count() //collect().size
	
    var err = newgraph.vertices.collect().map{ case (vid, vd) =>
        if (vid % 2 == 1) vd._4 else 0.0
    }.reduce(_ + _) / tri_size
    
        
    println("the err is %.2f".format(err))
	*/
    sc.stop();
    
  }
  
}
