package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.workload.Workload
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.sql.{DataFrame, SparkSession}

case class CacheTestResult(name: String, timestamp: Long, runTime1: Long, runTime2: Long, runTime3: Long)

case class CacheTest(name: String,
                    inputDir: Option[String],
                    workloadResultsOutputDir: Option[String],
                    sleepMs: Long) extends Workload {

  def this(m: Map[String, Any]) =
    this(name = getOrDefault(m, "name", "cachetest"),
      inputDir = m.get("input").map(_.asInstanceOf[String]),
      workloadResultsOutputDir = m.get("workloadresultsoutputdir").map(_.asInstanceOf[String]),
      sleepMs = getOrDefault(m, "sleepMs", 1000L))


  def doWorkload(df: Option[DataFrame], spark: SparkSession): DataFrame = {
    import spark.implicits._

    val cached= df.getOrElse(Seq.empty[(Int)].toDF).cache

    val (resultTime1, _) = time(cached.count)
    Thread.sleep(sleepMs)
    val (resultTime2, _) = time(cached.count)
    Thread.sleep(sleepMs)
    val (resultTime3, _) = time(cached.count)

    val now = System.currentTimeMillis()
    spark.createDataFrame(Seq(CacheTestResult("cachetest", now, resultTime1, resultTime2, resultTime3)))
  }
}
