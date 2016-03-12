
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

import com.google.common.io.{ByteStreams, Files}
import java.io.File
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql._
import org.apache.spark.sql.hive.HiveContext
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.storage.StorageLevel

object HiveFromSpark {
  case class Order(oid: Int,bid: Int, cts: String)
  case class OItem(iid: Int, oid: Int, gid: Int , gnum: Double, price: Double, gstore: Double)
  

  def main(args: Array[String]) {
  
    if (args.length != 3) {
      println("usage:<input>  <output> <numpar> <storageLevel> ")
      System.exit(0)
    }
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
	Logger.getLogger("org.apache.hadoop.hive").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
	val inputPath = args(0)
	val output = args(1)
	val numpar=args(2).toInt
   
    //val storageLevel=args(3)
   //val storageLevel="MEMORY_AND_DISK_SER"
   
   val storageLevel="MEMORY_AND_DISK"
  var sl:StorageLevel=StorageLevel.MEMORY_ONLY;
  if(storageLevel=="MEMORY_AND_DISK_SER")
    sl=StorageLevel.MEMORY_AND_DISK_SER
  else if(storageLevel=="MEMORY_AND_DISK")
    sl=StorageLevel.MEMORY_AND_DISK
    
	  sl=StorageLevel.MEMORY_AND_DISK
	println(s"storage level $sl")
	val sparkConf = new SparkConf().setAppName("HiveFromSpark")
    val sc = new SparkContext(sparkConf)
	
	val hiveContext = new HiveContext(sc)
       
 
    import hiveContext.implicits._
	import hiveContext.sql
	
	val orderRdd = sc.textFile(inputPath+"/OS_ORDER.txt",numpar).map{line => 
		val data=line.split("\\|")
		Order(data(0).toInt,data(1).toInt,data(2))
	}//.toDF()    	
    orderRdd.persist(sl)
	orderRdd.toDF.registerTempTable("orderTab")
	//hiveContext.cacheTable("orderTab")	
	
	val oitemRdd = sc.textFile(inputPath+"/OS_ORDER_ITEM.txt",numpar).map{line => 
		val data=line.split("\\|")
		OItem(data(0).toInt,data(1).toDouble.toInt,data(2).toInt,data(3).toDouble,data(4).toDouble,data(5).toDouble)
	}//.toDF()
    oitemRdd.persist(sl)
    oitemRdd.toDF.registerTempTable("oitemTab")
	
  //  hiveContext.cacheTable("oitemTab")	

    var cnt:Long=0;
    cnt = sql("SELECT COUNT(*) FROM orderTab").count()
    println(s"agg COUNT(*): $cnt")	
		
		cnt = sql("SELECT COUNT(*) FROM orderTab where bid>5000").count()
    println(s"agg COUNT(*) bid>5000: $cnt")	
		
    cnt=sql("SELECT * FROM oitemTab WHERE price>500").count()	
    println(s"select COUNT(*) price>250: $cnt")
	
		cnt=sql("SELECT * FROM oitemTab WHERE price>1000").count()	
    println(s"select COUNT(*) price>750: $cnt")
	
    cnt=sql("SELECT * FROM orderTab r JOIN oitemTab s ON r.oid = s.oid").count()	
	println(s"join COUNT(*): $cnt")
	

    sc.stop()
  }
}

	// The results of SQL queries are themselves RDDs and support all normal RDD functions.  The
    // items in the RDD are of type Row, which allows you to access each column by ordinal.
   // val rddFromSql = sql("SELECT key, value FROM records1 WHERE key < 10 ORDER BY key")

   // println("Result of RDD.map:")
	
    //val rddAsStrings = rddFromSql.map {
    //  case Row(key: Int, value: String) => s"Key: $key, Value: $value"
   // }
	
	
	//sql("CREATE TABLE IF NOT EXISTS order (oid INT, oc STRING, bid INT, cts STRING, pts STRING, ip STRING, ostat STRING)")
	//	sql("CREATE TABLE IF NOT EXISTS order (oid INT,  bid INT, cts STRING)")
	//sql("CREATE TABLE IF NOT EXISTS orderItem (iid INT, oid INT, gid INT , gnum DOUBLE, price DOUBLE, gprice DOUBLE, gstore DOUBLE)")
//	sql("CREATE TABLE IF NOT EXISTS orderItem (iid INT, oid INT, gid INT , gnum DOUBLE, price DOUBLE, gprice DOUBLE, gstore DOUBLE)")
    //sql(s"LOAD DATA LOCAL INPATH '${kv1File.getAbsolutePath}' INTO TABLE src")
	
	// Copy kv1.txt file from classpath to temporary directory
  //val kv1Stream = HiveFromSpark.getClass.getResourceAsStream("/kv1.txt")
  //val kv1File = File.createTempFile("kv1", "txt")
  //kv1File.deleteOnExit()
  //ByteStreams.copy(kv1Stream, Files.newOutputStreamSupplier(kv1File))
  
  	// A hive context adds support for finding tables in the MetaStore and writing queries
    // using HiveQL. Users who do not have an existing Hive deployment can still create a
    // HiveContext. When not configured by the hive-site.xml, the context automatically
    // creates metastore_db and warehouse in the current directory.
   