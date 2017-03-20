package com.ibm.sparktc.sparkbench.workload.mlworkloads

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadConfig, WorkloadKickoff}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._

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

//TODO put the defaults and common functions like getOrDefault in a utils project
object KMeansWorkloadDefaults {
  // The parameters for data generation. 100 million points (aka rows) roughly produces 36GB data size
  val NUM_OF_CLUSTERS: Int = 2
  val DIMENSIONS: Int = 20
  val SCALING: Double = 0.6
  val NUM_OF_PARTITIONS: Int = 2

  // Params for workload, in addition to some stuff up there ^^
  val MAX_ITERATION: Int = 2
  val SEED: Long = 127L
}

class KMeansWorkload(conf: WorkloadConfig, sparkSessOpt: Option[SparkSession] = None) extends Workload(conf, sparkSessOpt){

  import KMeansWorkloadDefaults._

  /*
      *****************************************************************************************
      LET'S CONSIDER:
      - Does timing the load, save, and testing really get us anything? Are these valuable?
      - Better to let users write their own workloads and it's on them to time the right stuff?
      *****************************************************************************************
   */

  override def doWorkload(df: DataFrame, spark: SparkSession): DataFrame = {
    val (loadtime, data) = load(df, spark) // necessary to time this?
    val (trainTime, model) = train(data, spark)
    val (testTime, _) = test(model, data, spark) // necessary?
    val (saveTime, _) = conf.workloadResultsOutputDir match {
      case Some(s) => save(data, model, spark)
      case _ => (null, Unit)
    }

    val schema = StructType(
      List(
        StructField("name", StringType, nullable = false),
        StructField("timestamp", LongType, nullable = false),
        StructField("load", LongType, nullable = true),
        StructField("train", LongType, nullable = true),
        StructField("test", LongType, nullable = true),
        StructField("save", LongType, nullable = true)
      )
    )

    val timeList = spark.sparkContext.parallelize(Seq(Row("kmeans", System.currentTimeMillis(), loadtime, trainTime, testTime, saveTime)))
    println(timeList.first())

    spark.createDataFrame(timeList, schema)
  }

  def load(df: DataFrame, spark: SparkSession): (Long, RDD[Vector]) = {
    time {
      val baseDS: RDD[Vector] = df.rdd.map(
        row => {
          val range = 1 to row.size
          val doublez: Array[Double] = range.map(i => {
            println(s"This row: $row\n i: $i")
            row.getDouble(i)
          }).toArray
          Vectors.dense(doublez)
        }
      )
      baseDS.cache()
    }
  }

  def getOrDefault[A](map: Map[String, Any], name: String, default: A): A = map.get(name) match {
    case Some(x) => x.asInstanceOf[A]
    case None => default
  }


  def train(df: RDD[Vector], spark: SparkSession): (Long, KMeansModel) = {
    time {
      KMeans.train(
        data = df,
        k = getOrDefault(conf.workloadSpecific, "k", KMeansWorkloadDefaults.NUM_OF_CLUSTERS),
        maxIterations = getOrDefault(conf.workloadSpecific, "maxIterations", KMeansWorkloadDefaults.MAX_ITERATION),
        initializationMode = KMeans.K_MEANS_PARALLEL,
        seed = getOrDefault(conf.workloadSpecific, "seed", KMeansWorkloadDefaults.SEED) )
    }
  }

  //Within Sum of Squared Errors
  def test(model: KMeansModel, df: RDD[Vector], spark: SparkSession): (Long, Double) = {
    time{ model.computeCost(df) }
  }

  def save(ds: RDD[Vector], model: KMeansModel, spark: SparkSession): (Long, Unit) = {
    val res = time {
      val vectorsAndClusterIdx: RDD[(String, Int)] = ds.map { point =>
        val prediction = model.predict(point)
        (point.toString, prediction)
      }
      import spark.implicits._
      // Already performed the match one level up so these are guaranteed to be Some(something)
      writeToDisk(vectorsAndClusterIdx.toDF(), conf.workloadResultsOutputDir.get, conf.workloadResultsOutputFormat.get)
    }
    ds.unpersist()
    res
  }

}
