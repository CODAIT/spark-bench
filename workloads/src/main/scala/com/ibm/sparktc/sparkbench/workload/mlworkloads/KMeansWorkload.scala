package com.ibm.sparktc.sparkbench.workload.mlworkloads

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadConfig}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.{getOrDefault, time}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk

import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import com.ibm.sparktc.sparkbench.utils.KMeansDefaults


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

class KMeansWorkload(conf: WorkloadConfig, sparkSessOpt: Option[SparkSession] = None) extends Workload(conf, sparkSessOpt){


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

  def train(df: RDD[Vector], spark: SparkSession): (Long, KMeansModel) = {
    time {
      KMeans.train(
        data = df,
        k = getOrDefault(conf.workloadSpecific, "k", KMeansDefaults.NUM_OF_CLUSTERS),
        maxIterations = getOrDefault(conf.workloadSpecific, "maxIterations", KMeansDefaults.MAX_ITERATION),
        initializationMode = KMeans.K_MEANS_PARALLEL,
        seed = getOrDefault(conf.workloadSpecific, "seed", KMeansDefaults.SEED) )
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
      writeToDisk(conf.workloadResultsOutputFormat.get, conf.workloadResultsOutputDir.get, vectorsAndClusterIdx.toDF())
    }
    ds.unpersist()
    res
  }

}
