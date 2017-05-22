//package com.ibm.sparktc.sparkbench.workload.sql
//
//import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
//import com.ibm.sparktc.sparkbench.utils.SparkFuncs._
//import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadConfig}
//import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
//import org.apache.spark.sql.{DataFrame, Row, SparkSession}
//
//case class SQLSleepWorkloadConf(
//                                   name: String,
//                                   inputDir: Option[String],
//                                   workloadResultsOutputDir: Option[String] = None,
//                                   queryStr: String,
//                                   sleepBeforeMS: Long = 0L,
//                                   sleepAfterMS: Long = 0L
//                                 ) extends WorkloadConfig {
//
//  def this(m: Map[String, Any], spark: SparkSession) = {
//    this(
//      verifyOrThrow(m, "name", "sql", s"Required field name does not match"),
//      inputDir = Some(getOrThrow(m, "input").asInstanceOf[String]),
//      workloadResultsOutputDir = getOrDefault[Option[String]](m, "workloadresultsoutputdir", None),
//      getOrThrow(m, "query").asInstanceOf[String],
//      getOrDefault(m, "sleepbeforems", 0L, any2Int2Long),
//      getOrDefault(m, "sleepafterms", 0L, any2Int2Long)
//    )
//  }
//
//  override def toMap(cc: AnyRef): Map[String, Any] = super.toMap(this)
//}
//
//class SQLSleepWorkload (conf: SQLSleepWorkloadConf, spark: SparkSession) extends Workload[SQLWorkloadConf](conf, spark) {
//
//  def loadFromDisk(spark: SparkSession): (Long, DataFrame) = time {
//    load(spark, conf.inputDir.get)
//  }
//
//  def query(df: DataFrame, spark: SparkSession): (Long, DataFrame) = time {
//    df.createOrReplaceTempView("input")
//    spark.sqlContext.sql(conf.queryStr)
//  }
//
//  def save(res: DataFrame, where: String, spark: SparkSession): (Long, Unit) = time {
//    writeToDisk(where, res, spark)
//  }
//
//  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
//    Thread.sleep(conf.sleepBeforeMS)
//
//    val (loadtime, df) = loadFromDisk(spark)
//    val (querytime, res) = query(df, spark)
//    val (savetime, _) = conf.workloadResultsOutputDir match {
//      case Some(dir) => save(res, dir, spark)
//      case _ => (0L, Unit)
//    }
//
//    Thread.sleep(conf.sleepAfterMS)
//
//    val total = loadtime + querytime + savetime
//
//    val schema = StructType(
//      List(
//        StructField("name", StringType, nullable = false),
//        StructField("timestamp", LongType, nullable = false),
//        StructField("load", LongType, nullable = true),
//        StructField("query", LongType, nullable = false),
//        StructField("save", LongType, nullable = true),
//        StructField("total_runtime", LongType, nullable = false)
//      )
//    )
//
//    val timeList = spark.sparkContext.parallelize(Seq(Row("sql", System.currentTimeMillis(), loadtime, querytime, savetime, total)))
//
//    spark.createDataFrame(timeList, schema)
//  }
//
//}
//
