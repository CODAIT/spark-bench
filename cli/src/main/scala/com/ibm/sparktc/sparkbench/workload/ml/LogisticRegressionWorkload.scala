package com.ibm.sparktc.sparkbench.workload.ml

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.workload.Workload
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator => BCE}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types._

// ¯\_(ツ)_/¯
// the logic for this workload came from:
// https://github.com/szilard/benchm-ml/blob/master/1-linear/5-spark.txt
// ¯\_(ツ)_/¯

case class LogisticRegressionWorkload(
  name: String,
  inputDir: Option[String],
  workloadResultsOutputDir: Option[String],
  trainFile: String = "spark-train-1m.csv",
  testFile: String = "spark-test-1m.csv",
  numPartitions: Int = 32,
  cacheEnabled: Boolean = true
  ) extends Workload {

  def this(m: Map[String, Any]) = this(
    name = verifyOrThrow(m, "name", "lr-bml", s"Required field name does not match"),
    inputDir = Some(getOrThrow(m, "input").asInstanceOf[String]),
    workloadResultsOutputDir = getOrDefault[Option[String]](m, "workloadresultsoutputdir", None),
    trainFile = getOrThrow(m, "trainfile").asInstanceOf[String],
    testFile = getOrThrow(m, "testfile").asInstanceOf[String],
    numPartitions = getOrDefault(m, "numpartitions", 32),
    cacheEnabled = getOrDefault(m, "cacheenabled", true)
  )

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
    val (ltrainTime, d_train) = ld(s"${inputDir.get}/$trainFile")(spark)
    val (ltestTime, d_test) = ld(s"${inputDir.get}/$testFile")(spark)
    val (countTime, (trainCount, testCount)) = time { (d_train.count(), d_test.count()) }
    val (trainTime, model) = time(new LogisticRegression().setTol(1e-4).fit(d_train))
    val (testTime, areaUnderROC) = time(new BCE().setMetricName("areaUnderROC").evaluate(model.transform(d_test)))

    val loadTime = ltrainTime + ltestTime
    val timeList = spark.sparkContext.parallelize(
      Seq(
        Row(
          name,
          spark.sparkContext.applicationId,
          startTime,
          trainFile,
          trainCount,
          trainTime,
          testFile,
          testCount,
          testTime,
          loadTime,
          countTime,
          loadTime + trainTime + testTime,
          areaUnderROC
        )
      )
    )

    spark.createDataFrame(timeList,
      StructType(
        List(
          StructField("name", StringType, nullable = false),
          StructField("appid", StringType, nullable = false),
          StructField("start_time", LongType, nullable = false),
          StructField("train_file", StringType, nullable = false),
          StructField("train_count", LongType, nullable = false),
          StructField("train_time", LongType, nullable = false),
          StructField("test_file", StringType, nullable = false),
          StructField("test_count", LongType, nullable = false),
          StructField("test_time", LongType, nullable = false),
          StructField("load_time", LongType, nullable = false),
          StructField("count_time", LongType, nullable = false),
          StructField("total_runtime", LongType, nullable = false),
          StructField("area_under_roc", DoubleType, nullable = false)
        )
      )
    )
  }
}