package com.ibm.sparktc.sparkbench.workload.mlworkloads

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadConfig}
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

class KMeansWorkload(conf: WorkloadConfig) extends Workload(conf){

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
    val (saveTime, _) = save(data, model, spark) // necessary?

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
      val baseDS: RDD[Vector] = df.rdd.map(row => {
        val range = 1 to row.size
        val doublez = range.map(i => row.getDouble(i)).toArray
        Vectors.dense( doublez)
      }
      )
      baseDS.cache()
    }
    //    val loadTime = (System.currentTimeMillis() - start).toDouble / 1000.0
  }

  def train(df: RDD[Vector], spark: SparkSession): (Long, KMeansModel) = {
    time {
      KMeans.train(
        data = df,
        k = conf.workloadSpecific.get("K").asInstanceOf[Int],
        maxIterations = conf.workloadSpecific.get("maxIterations").asInstanceOf[Int],
        initializationMode = KMeans.K_MEANS_PARALLEL,
        seed = conf.workloadSpecific.getOrElse("seed", "127").toString.toLong) //TODO ugly haxx
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
      writeToDisk(vectorsAndClusterIdx.toDF(), conf.workloadResultsOutputDir, conf.workloadResultsOutputFormat)
    }
    ds.unpersist()
    res
  }

}
