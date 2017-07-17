//package com.ibm.sparktc.sparkbench.workload.sql
//
//import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
//import com.ibm.sparktc.sparkbench.utils.SparkFuncs._
//import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadConfig}
//import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
//import org.apache.spark.sql.{DataFrame, Row, SparkSession}
//
//import scala.util.Random
//
//case class SQLRandomSleepWorkloadConf(
//                                   override val name: String,
//                                   override val inputDir: Option[String],
//                                   override val workloadResultsOutputDir: Option[String] = None,
//                                   override val queryStr: String,
//                                   override val sleepBeforeMS: Long,
//                                   override val sleepAfterMS: Long
//                                 ) extends SQLSleepWorkloadConf(
//                                            name,
//                                            inputDir,
//                                            workloadResultsOutputDir,
//                                            queryStr,
//                                            sleepBeforeMS,
//                                            sleepAfterMS
//                                            ) {
//  def randomLong(): Long = {
//    val start = 1L
//    val end = 60000L //60 seconds in millis
//    (start + (Random.nextDouble() * (end - start) + start)).toLong
//  }
//
//  def this(m: Map[String, Any], spark: SparkSession) = {
//    this(
//      verifyOrThrow(m, "name", "sqlrandomsleep", s"Required field name does not match"),
//      inputDir = Some(getOrThrow(m, "input").asInstanceOf[String]),
//      workloadResultsOutputDir = getOrDefault[Option[String]](m, "workloadresultsoutputdir", None),
//      getOrThrow(m, "query").asInstanceOf[String],
//      randomLong(),
//      randomLong()
//    )
//  }
//
//  override def toMap(cc: AnyRef): Map[String, Any] = super.toMap(this)
//}
//
//class SQLRandomSleepWorkload (conf: SQLRandomSleepWorkloadConf, spark: SparkSession) extends SQLSleepWorkload(conf, spark)
//
