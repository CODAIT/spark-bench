package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.workload.Workload
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.utils.TimedSleepDefaults
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}

case class PartitionAndSleepWorkload(name: String,
                                     input: Option[String] = None,
                                     workloadResultsOutputDir: Option[String] = None,
                                     partitions: Int,
                                     sleepMS: Long) extends Workload {

  def this(m: Map[String, Any]) = this(
      verifyOrThrow(m, "name", "timedsleep", s"Required field name does not match"),
      None,
      None,
      getOrDefault(m, "partitions", TimedSleepDefaults.PARTITIONS),
      getOrDefault[Long](m, "sleepms", TimedSleepDefaults.SLEEPMS, any2Int2Long))

  def doStuff(spark: SparkSession): (Long, Unit) = time {

    val ms = sleepMS
    val stuff: RDD[Int] = spark.sparkContext.parallelize(0 until partitions * 100, partitions)

    val cool: RDD[(Int, Int)] = stuff.map { i =>
      Thread.sleep(ms)
      (i % 10, i + 42)
    }

    val yeah = cool.reduceByKey(_ + _)
    yeah.collect()
  }

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val (t, _) = doStuff(spark)

    val schema = StructType(
      List(
        StructField("name", StringType, nullable = false),
        StructField("timestamp", LongType, nullable = false),
        StructField("runtime", LongType, nullable = false)
      )
    )

    val timeList = spark.sparkContext.parallelize(Seq(Row("timedsleep", System.currentTimeMillis(), t)))

    spark.createDataFrame(timeList, schema)
  }
}

