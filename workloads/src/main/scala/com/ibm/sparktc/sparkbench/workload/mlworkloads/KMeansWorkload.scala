package com.ibm.sparktc.sparkbench.workload.mlworkloads

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadConfig}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.Vectors

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

  override def doWorkload(df: DataFrame, spark: SparkSession) = {
    val (loadtime, data) = load(df, spark) // necessary to time this?
    val (trainTime, model) = train(data, spark)
    val (testTime) = test(model, data, spark) // necessary?
    val (saveTime) = save(data, spark) // necessary?
  }

  def load(df: DataFrame, spark: SparkSession): (Long, Dataset[Vector]) = {
    time {
      val baseRDD: Dataset[Vector] = df.map(row => {
        Vectors.dense(row.toSeq.toArray.map({
          case s: String => s.toDouble
          case l: Long => l.toDouble
          case _ => 0.0
        }))
      })
      baseRDD.cache()
    }
    //    val loadTime = (System.currentTimeMillis() - start).toDouble / 1000.0
  }

  def train(df: Dataset[Vector], spark: SparkSession): (Long, KMeansModel) = {
    time {
      KMeans.train(
        data = df.rdd,
        k = conf.workloadSpecific.get("K").asInstanceOf[Int],
        maxIterations = conf.workloadSpecific.get("maxIterations").asInstanceOf[Int],
        initializationMode = KMeans.K_MEANS_PARALLEL,
        seed = conf.workloadSpecific.getOrElse("seed", "127").toString.toLong) //TODO ugly haxx
    }
  }

  //Within Sum of Squared Errors
  def test(model: KMeansModel, df: Dataset[Vector], spark: SparkSession): Unit = {
    time{ model.computeCost(df.rdd) }

  }

  def save(df: Dataset[Vector], spark: SparkSession): Unit = ???

}
