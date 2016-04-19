
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

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.rdd._
import scala.reflect._
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.storage.StorageLevel;
// One method for defining the schema of an RDD is to make a case class with the desired column
// names and types.
case class Record(key: Int, value: String)
case class Order(oid: Int,bid: Int, cts: String)
case class OItem(iid: Int, oid: Int, gid: Int , gnum: Double, price: Double, gstore: Double)
	
object RDDRelation {
  def main(args: Array[String]) {
    if (args.length !=3) {
      println("usage: <input> <output>   <numPartition> ")
      System.exit(0)
    }
	Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    val conf = new SparkConf
    conf.setAppName("Spark RDDRelation Application")
    val sc = new SparkContext(conf)

    val inputPath = args(0)
    val output = args(1)
    val numpar=args(2).toInt
   
    val storageLevel="MEMORY_AND_DISK_SER"
  var sl:StorageLevel=StorageLevel.MEMORY_ONLY;
  if(storageLevel=="MEMORY_AND_DISK_SER")
    sl=StorageLevel.MEMORY_AND_DISK_SER
  else if(storageLevel=="MEMORY_AND_DISK")
    sl=StorageLevel.MEMORY_AND_DISK
    
	sl=StorageLevel.MEMORY_AND_DISK_SER
	
    val sqlContext = new SQLContext(sc)


    import sqlContext.implicits._
	

	val orderRdd = sc.textFile(inputPath+"/OS_ORDER.txt",numpar).map{line => 
		//val data=line.split("\\s+")
		val data=line.split("\\|")
		Order(data(0).toInt,data(1).toInt,data(2))
	}    
	orderRdd.toDF.registerTempTable("orderTab")
	orderRdd.persist(sl)

    val oitemRdd = sc.textFile(inputPath+"/OS_ORDER_ITEM.txt",numpar).map{line => 
		val data=line.split("\\|")
		OItem(data(0).toInt,data(1).toDouble.toInt,data(2).toInt,data(3).toDouble,data(4).toDouble,data(5).toDouble)
	}
    oitemRdd.toDF.registerTempTable("oitemTab")	
	oitemRdd.persist(sl)
	

	
	var cnt:Long=0;  	    
  cnt = sqlContext.sql("SELECT COUNT(*) FROM orderTab").count()
  println(s"agg COUNT(*): $cnt")	
	
	cnt = sqlContext.sql("SELECT COUNT(*) FROM orderTab where bid>5000").count()
  println(s"agg COUNT(*) bid>5000: $cnt")	
		
  cnt=sqlContext.sql("SELECT * FROM oitemTab WHERE price>250").count()	
  println(s"select COUNT(*): $cnt")
	
	cnt=sqlContext.sql("SELECT * FROM oitemTab WHERE price>500").count()	
  println(s"select COUNT(*): $cnt")
	
  cnt=sqlContext.sql("SELECT * FROM orderTab r JOIN oitemTab s ON r.oid = s.oid").count()	
	println(s"join COUNT(*): $cnt")
	
    sc.stop()
  }
}
//cnt=sql("SELECT * FROM oitemTab WHERE price>500").count()
	//collect().foreach(_=>cnt=cnt+1)
    //var cnt=0;
	//println("Result of 'SELECT *': ")
    //sql("SELECT * FROM oitemTab WHERE price>500").collect().foreach(_=>cnt=cnt+1)
    //println(s"select COUNT(*): $cnt")
	
