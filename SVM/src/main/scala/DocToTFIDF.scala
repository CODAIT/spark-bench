
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
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext,SparkConf} 
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.feature.IDF
import org.apache.spark.mllib.linalg.Vector
import scala.util.Random
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.storage.StorageLevel




 object DocToTFIDF {
 
    def main(args: Array[String]) {
    if (args.length != 3) {
	  println("usage: <input> <output>  <numPar>")      
      System.exit(0)
    }
	Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val conf = new SparkConf
    conf.setAppName("Spark Tf-idf Application")
    val sc = new SparkContext(conf)
    
	val input = args(0) 
    val output = args(1)
	val numPar=args(2).toInt
		
		
	// Load documents (one per line).
	val parsedData:RDD[(Double,Seq[String])] = sc.textFile(input,numPar).filter(_.split("::::",2).length==2).map{ line =>
		val data=line.split("::::",2)	  
		val cate=data(0).hashCode().toDouble
		val doc=data(1).split(" ").toSeq
		(cate,doc)
	  }
	
	parsedData.persist(StorageLevel.MEMORY_AND_DISK)
	
	val documents: RDD[Seq[String]] = parsedData.map{ case (cate,doc) => doc}	
	val hashingTF = new HashingTF()
	val tf: RDD[Vector] = hashingTF.transform(documents)
	tf.cache()
	val idf = new IDF(minDocFreq = 2).fit(tf)
	val tfidf: RDD[Vector] = idf.transform(tf)
	
	val results: RDD[LabeledPoint]=tfidf.map{point =>		
		val rnd = new Random(System.currentTimeMillis())
		val yD=rnd.nextGaussian() 
		val y = if (yD < 0) 0.0 else 1.0
		new LabeledPoint(y,point)
		
	}


	
	results.saveAsTextFile(output)
	
    sc.stop();
    
  }
  
}
//new LabeledPoint(y,Vectors.dense(point.toArray))
