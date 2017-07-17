package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.sql.{DataFrame, SparkSession}

case class SleepResult(
                      name: String,
                      timestamp: Long,
                      total_runtime: Long
                      )

object Sleep extends WorkloadDefaults {
  val name = "sleep"
  def apply(m: Map[String, Any]) =
    new Sleep(input = m.get("input").map(_.asInstanceOf[String]),
      output = None,
      sleepMS = (m.get("sleepms"), m.get("maxsleepms")) match {
        case (Some(l), _) => any2Int2Long(l)
        case (None, Some(l)) => randomLong(max = any2Int2Long(l))
        case (_, _) => randomLong(max = 3600000L) //one hour
      }
    )
}

case class Sleep(
                input: Option[String] = None,
                output: Option[String] = None,
                sleepMS: Long
              ) extends Workload {

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val timestamp = System.currentTimeMillis()
    val (t, _) = time {
      Thread.sleep(sleepMS)
    }

    spark.createDataFrame(Seq(SleepResult("sleep", timestamp, t)))
  }

}

