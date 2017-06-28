package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.cli.SuiteArgs
import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}

object PartitionAndSleepWorkload extends WorkloadDefaults {
  val name = "timedsleep"
  val PARTITIONS: Int = 48
  val SLEEPMS: Long = 12000L
  override val subcommand = new SuiteArgs("timedsleep"){
    val partitions = opt[List[Int]](short = 'p', default = Some(List(PARTITIONS)), descr = "how many partitions to spawn")
    val sleepMS = opt[List[Long]](short = 't', default = Some(List(SLEEPMS)), descr = "amount of time a thread will sleep, in milliseconds")
  }
}

case class PartitionAndSleepWorkload(inputDir: Option[String] = None,
                                     workloadResultsOutputDir: Option[String] = None,
                                     partitions: Int,
                                     sleepMS: Long) extends Workload {

  def this(m: Map[String, Any]) = this(
      None,
      None,
      getOrDefault(m, "partitions", PartitionAndSleepWorkload.PARTITIONS),
      getOrDefault[Long](m, "sleepms", PartitionAndSleepWorkload.SLEEPMS, any2Int2Long))

  def doStuff(spark: SparkSession): (Long, Unit) = time {

    val ms = sleepMS
    val stuff: RDD[Int] = spark.sparkContext.parallelize(0 until partitions, partitions)

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

