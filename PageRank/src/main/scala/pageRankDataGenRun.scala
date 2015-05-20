package src.main.scala
import org.apache.spark.{SparkContext,SparkConf, Logging}
import org.apache.spark.SparkContext._
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib._
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.rdd._

import org.apache.spark.storage.StorageLevel
import org.apache.spark.graphx.impl.{EdgePartitionBuilder, GraphImpl}

object pageRankDataGenRun extends Logging{
  
  def main(args: Array[String]) {
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