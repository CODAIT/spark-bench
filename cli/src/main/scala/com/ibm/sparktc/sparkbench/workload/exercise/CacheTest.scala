package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.sql.{DataFrame, SparkSession}

case class CacheTestResult(name: String, timestamp: Long, runTime1: Long, runTime2: Long, runTime3: Long)

object CacheTest extends WorkloadDefaults {
  val name = "cachetest"
  def apply(m: Map[String, Any]) =
    new CacheTest(input = m.get("input").map(_.asInstanceOf[String]),
      output = m.get("workloadresultsoutputdir").map(_.asInstanceOf[String]),
      sleepMs = getOrDefault[Long](m, "sleepMs", 1000L))
}

case class CacheTest(input: Option[String],
                    output: Option[String],
                    sleepMs: Long) extends Workload {

  def doWorkload(df: Option[DataFrame], spark: SparkSession): DataFrame = {
    import spark.implicits._

    val cached = df.getOrElse(Seq.empty[(Int)].toDF).cache

    val (resultTime1, _) = time(cached.count)
    Thread.sleep(sleepMs)
    val (resultTime2, _) = time(cached.count)
    Thread.sleep(sleepMs)
    val (resultTime3, _) = time(cached.count)

    val now = System.currentTimeMillis()
    spark.createDataFrame(Seq(CacheTestResult("cachetest", now, resultTime1, resultTime2, resultTime3)))
  }
}
