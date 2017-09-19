/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import breeze.stats.distributions.{Poisson, Rand}
import com.ibm.sparktc.sparkbench.utils.SparkBenchException

case class SleepResult(
                        name: String,
                        timestamp: Long,
                        total_runtime: Long
                      )

object Sleep extends WorkloadDefaults {

  val name = "sleep"
  def apply(m: Map[String, Any]): Sleep = {

    val sleepMS: Option[Long] = m.get("sleepms").map(any2Long)
    val distribution: Option[String] = m.get("distribution").asInstanceOf[Option[String]]
    val distributionMean: Option[Double] = m.get("mean").asInstanceOf[Option[Double]]
    val distributionStd: Option[Double] = m.get("std").asInstanceOf[Option[Double]]
    val distributionMin: Option[Long] = m.get("min").map(any2Long)
    val distributionMax: Option[Long] = m.get("max").map(any2Long)

    def buildWithDist(dist: String) = dist match {
      case "uniform" => buildWithUniform(distributionMin, distributionMax)
      case "poisson" => buildWithPoisson(distributionMean)
      case "gaussian" => buildWithGaussian(distributionMean, distributionStd)
      case other => throw SparkBenchException(
        s"""
           |Distribution $other is not implemented for the Sleep workload.
           |Please see documentation for available distributions or specify "sleepMS".
         """.stripMargin)
    }

    (sleepMS, distribution) match {
      case (Some(sleep), None) => buildWithSleepMS(sleep)
      case (None, Some(dist)) => buildWithDist(dist)
      case _ => throw SparkBenchException("The Sleep workload requires either a time specified by sleepMS " +
        "or a distribution from which to choose a time. Please modify your config file.")
    }
  }

  def buildWithSleepMS(long: Long): Sleep = {
    if (long <= 0 )
      throw SparkBenchException("The time specified by sleepMS must be greater than or equal to zero. " +
        "Please modify your config file.")
    else
      new Sleep( sleepMS = long )

  }


  def buildWithPoisson(mean: Option[Double]): Sleep = {
    def poissonDraw(mean: Double): Long = {
      val dist = Poisson(mean)
      dist.draw().toLong
    }

    val sleepTime = mean match {
      case None => throw SparkBenchException("Using the Poisson distribution for the Sleep workload " +
        "requires a value for \"distributionMean\". Please modify your config file.")
      case Some(m) => poissonDraw(m)
    }

    Sleep(
      sleepMS = sleepTime,
      distribution = Some("poisson"),
      distributionMean = mean
    )
  }


  def buildWithUniform(min: Option[Long], max: Option[Long]): Sleep = {
    def uniformRandomDraw(max: Long): Long = {
      val dist = Rand.randLong(max)
      dist.draw()
    }

    val minimum: Long = getOrDefaultOpt[Long](min, 0L, any2Long)
    val adjustedMax: Long = max match {
      case None => throw SparkBenchException("Using the uniform distribution for the Sleep workload " +
        "requires a value for \"distributionMax\". Please modify your config file.")
      case Some(m) => any2Long(m) - minimum
    }

    val sleepTime = uniformRandomDraw(adjustedMax) + minimum

    Sleep(
      sleepMS = sleepTime,
      distribution = Some("uniform"),
      distributionMin = Some(minimum),
      distributionMax = max
    )
  }

  def buildWithGaussian(mean: Option[Double], std: Option[Double]): Sleep = {
    def gaussianDraw(mean: Double, std: Double): Long = {
      val dist = Rand.gaussian(mean, std)
      dist.draw().toLong
    }
    val sleepTime = (mean, std) match {
      case (Some(m),Some(s)) => gaussianDraw(m, s)
      case _ => throw SparkBenchException("Using the gaussian distribution for the Sleep workload " +
        "requires a value for \"distributionMean\" and \"distributionMax\". Please modify your config file.")
    }
    Sleep(
      sleepMS = sleepTime,
      distribution = Some("gaussian"),
      distributionMean = mean,
      distributionStd = std
    )
  }



}

case class Sleep(
                input: Option[String] = None,
                output: Option[String] = None,
                sleepMS: Long,
                distribution: Option[String] = None,
                distributionMean: Option[Double] = None,
                distributionStd: Option[Double] = None,
                distributionMin: Option[Long] = None,
                distributionMax: Option[Long] = None
              ) extends Workload {

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val timestamp = System.currentTimeMillis()
    val (t, _) = time {
      Thread.sleep(sleepMS)
    }

    spark.createDataFrame(Seq(SleepResult("sleep", timestamp, t)))
  }

}

