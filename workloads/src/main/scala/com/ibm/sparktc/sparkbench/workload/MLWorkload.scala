package com.ibm.sparktc.sparkbench.workload

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

abstract class MLWorkload(conf: WorkloadConfig) extends Workload(conf){

  def load(df: DataFrame, spark: SparkSession)
  def train(df: Dataset[Vector], spark: SparkSession)
  def test(df: Dataset[Vector], spark: SparkSession)
  def save(df: Dataset[Vector], spark: SparkSession)

}
