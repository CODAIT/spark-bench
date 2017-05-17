package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadConfig}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import scala.util.Random

case class SleepWorkloadConf(
                                          name: String,
                                          inputDir: Option[String] = None,
                                          workloadResultsOutputDir: Option[String] = None,
                                          sleepMS: Long
                                        ) extends WorkloadConfig {



//  def sleepTime(m: Map[String, Any]): Long = m.get("sleepms") match {
//    case Some(l) => any2Int2Long(l)
//    case None => getOrDefault[Long](m, "maxsleepms", 60000L, any2Int2Long)
//  }

  def this(m: Map[String, Any], spark: SparkSession) = {
    this(
      verifyOrThrow(m, "name", "sleep", s"Required field name does not match"),
      None,
      None,
      (m.get("sleepms"), m.get("maxsleepms")) match {
        case (Some(l), _) => any2Int2Long(l)
        case (None, Some(l)) => randomLong(max = any2Int2Long(l))
        case (_, _) => randomLong(max = 3600000L) //one hour
      }
    )
  }

  override def toMap(cc: AnyRef): Map[String, Any] = super.toMap(this)
}

class Sleep(conf: SleepWorkloadConf, spark: SparkSession) extends Workload[SleepWorkloadConf](conf, spark) {

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val timestamp = System.currentTimeMillis()
    val (t, _) = time {
      Thread.sleep(conf.sleepMS)
    }

    val schema = StructType(
      List(
        StructField("name", StringType, nullable = false),
        StructField("timestamp", LongType, nullable = false),
        StructField("total_runtime", LongType, nullable = false)
      )
    )

    val timeList = spark.sparkContext.parallelize(Seq(Row("timedsleep", timestamp, t)))

    spark.createDataFrame(timeList, schema)
  }

}

