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

package com.ibm.sparktc.sparkbench.workload.ml

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator => BCE}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

// ¯\_(ツ)_/¯
// the logic for this workload came from:
// https://github.com/szilard/benchm-ml/blob/master/1-linear/5-spark.txt
// ¯\_(ツ)_/¯

case class LogisticRegressionResult(
                                     name: String,
                                     appid: String,
                                     start_time: Long,
                                     input: String,
                                     train_count: Long,
                                     train_time: Long,
                                     test_file: String,
                                     test_count: Long,
                                     test_time: Long,
                                     load_time: Long,
                                     count_time: Long,
                                     total_runtime: Long,
                                     area_under_roc: Double
                                   )

object LogisticRegressionWorkload extends WorkloadDefaults {
  val name = "lr-bml"
  def apply(m: Map[String, Any]) = new LogisticRegressionWorkload(
    input = Some(getOrThrow(m, "input").asInstanceOf[String]),
    output = getOrDefault[Option[String]](m, "workloadresultsoutputdir", None),
    testFile = getOrThrow(m, "testfile").asInstanceOf[String],
    numPartitions = getOrDefault[Int](m, "numpartitions", 32),
    cacheEnabled = getOrDefault[Boolean](m, "cacheenabled", true)
  )

}

case class LogisticRegressionWorkload(
                                       input: Option[String],
                                       output: Option[String],
                                       testFile: String,
                                       numPartitions: Int,
                                       cacheEnabled: Boolean
  ) extends Workload {

  private[ml] def load(filename: String)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    spark.sparkContext.textFile(filename)
      .map { line =>
        val vv = line.split(',').map(_.toDouble)
        val label = vv(0)
        val features = Vectors.dense(vv.slice(1, vv.length)).toSparse
        (label, features)
      }.toDF("label", "features")
  }

  private[ml] def ld(fn: String)(implicit spark: SparkSession) = time {
    val ds = load(fn)(spark).repartition(numPartitions)
    if (cacheEnabled) ds.cache
    ds
  }

  override def doWorkload(df: Option[DataFrame], spark: SparkSession): DataFrame = {
    val startTime = System.currentTimeMillis
    val (ltrainTime, d_train) = ld(s"${input.get}")(spark)
    val (ltestTime, d_test) = ld(s"$testFile")(spark)
    val (countTime, (trainCount, testCount)) = time { (d_train.count(), d_test.count()) }
    val (trainTime, model) = time(new LogisticRegression().setTol(1e-4).fit(d_train))
    val (testTime, areaUnderROC) = time(new BCE().setMetricName("areaUnderROC").evaluate(model.transform(d_test)))

    val loadTime = ltrainTime + ltestTime

    //spark.createDataFrame(Seq(SleepResult("sleep", timestamp, t)))

    spark.createDataFrame(Seq(LogisticRegressionResult(
      name = "lr-bml",
      appid = spark.sparkContext.applicationId,
      startTime,
      input.get,
      train_count = trainCount,
      trainTime,
      testFile,
      test_count = testCount,
      testTime,
      loadTime,
      countTime,
      loadTime + trainTime + testTime,
      areaUnderROC
    )))
  }
}
