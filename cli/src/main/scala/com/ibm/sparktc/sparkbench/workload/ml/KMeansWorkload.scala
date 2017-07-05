package com.ibm.sparktc.sparkbench.workload.ml

import com.ibm.sparktc.sparkbench.workload.{Suite, Workload, WorkloadDefaults}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._

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

object KMeansWorkload extends WorkloadDefaults {
  val name = "kmeans"
  // The parameters for data generation. 100 million points (aka rows) roughly produces 36GB data size
  val NUM_OF_CLUSTERS: Int = 2
  val DIMENSIONS: Int = 20
  val SCALING: Double = 0.6
  val NUM_OF_PARTITIONS: Int = 2

  // Params for workload, in addition to some stuff up there ^^
  val MAX_ITERATION: Int = 2
  val SEED: Long = 127L

  def apply(m: Map[String, Any]) = new KMeansWorkload(
    input = Some(getOrThrow(m, "input").asInstanceOf[String]),
    workloadResultsOutputDir = getOrDefault[Option[String]](m, "workloadresultsoutputdir", None),
    k = getOrDefault(m, "k", NUM_OF_CLUSTERS),
    seed = getOrDefault(m, "seed", SEED, any2Int2Long),
    maxIterations = getOrDefault(m, "maxiterations", MAX_ITERATION))

}

case class KMeansWorkload(input: Option[String],
                          workloadResultsOutputDir: Option[String],
                          k: Int,
                          seed: Long,
                          maxIterations: Int) extends Workload {

  override def doWorkload(df: Option[DataFrame], spark: SparkSession): DataFrame = {
    val timestamp = System.currentTimeMillis()

    val (loadtime, data) = loadToCache(df.get, spark) // Should fail loudly if df == None
    val (trainTime, model) = train(data, spark)
    val (testTime, _) = test(model, data, spark)
    val (saveTime, _) = workloadResultsOutputDir match {
      case Some(_) => save(data, model, spark)
      case _ => (null, Unit)
    }

    val total = loadtime + trainTime + testTime
                + (if (saveTime == null) 0L else saveTime.asInstanceOf[Long])

    val schema = StructType(
      List(
        StructField("name", StringType, nullable = false),
        StructField("timestamp", LongType, nullable = false),
        StructField("load", LongType, nullable = true),
        StructField("train", LongType, nullable = true),
        StructField("test", LongType, nullable = true),
        StructField("save", LongType, nullable = true),
        StructField("total_runtime", LongType, nullable = false)
      )
    )

    val timeList = spark.sparkContext.parallelize(Seq(Row("kmeans", timestamp, loadtime, trainTime, testTime, saveTime, total)))
    //println(timeList.first())

    spark.createDataFrame(timeList, schema)
  }

  def loadToCache(df: DataFrame, spark: SparkSession): (Long, RDD[Vector]) = {
    time {
      val baseDS: RDD[Vector] = df.rdd.map(
        row => {
          val range = 0 until row.size
          val doublez: Array[Double] = range.map(i => {
            val x = row.getDouble(i)
            x
          }).toArray
          Vectors.dense(doublez)
        }
      )
      baseDS.cache()
    }
  }

  def train(df: RDD[Vector], spark: SparkSession): (Long, KMeansModel) = {
    time {
      KMeans.train(
        data = df,
        k = k,
        maxIterations = maxIterations,
        initializationMode = KMeans.K_MEANS_PARALLEL,
        seed = seed)
    }
  }

  //Within Sum of Squared Errors
  def test(model: KMeansModel, df: RDD[Vector], spark: SparkSession): (Long, Double) = time {
    model.computeCost(df)
  }

  def save(ds: RDD[Vector], model: KMeansModel, spark: SparkSession): (Long, Unit) = {
    val res = time {
      val vectorsAndClusterIdx: RDD[(String, Int)] = ds.map { point =>
        val prediction = model.predict(point)
        (point.toString, prediction)
      }
      import spark.implicits._
      // Already performed the match one level up so these are guaranteed to be Some(something)
      writeToDisk(workloadResultsOutputDir.get, vectorsAndClusterIdx.toDF(), spark = spark)
    }
    ds.unpersist()
    res
  }
}
