package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.workload.Workload
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.sql.{DataFrame,  SparkSession}

case class SleepResult(
                      name: String,
                      timestamp: Long,
                      total_runtime: Long
                      )

case class Sleep(
                                          name: String,
                                          inputDir: Option[String] = None,
                                          workloadResultsOutputDir: Option[String] = None,
                                          sleepMS: Long
                                        ) extends Workload {

  def this(m: Map[String, Any]) =
    this(name = getOrDefault(m, "name", "cachetest"),
      inputDir = m.get("input").map(_.asInstanceOf[String]),
      workloadResultsOutputDir = None,
      sleepMS = (m.get("sleepms"), m.get("maxsleepms")) match {
        case (Some(l), _) => any2Int2Long(l)
        case (None, Some(l)) => randomLong(max = any2Int2Long(l))
        case (_, _) => randomLong(max = 3600000L) //one hour
      }
    )


  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val timestamp = System.currentTimeMillis()
    val (t, _) = time {
      Thread.sleep(sleepMS)
    }

    spark.createDataFrame(Seq(SleepResult("sleep", timestamp, t)))
  }

}

