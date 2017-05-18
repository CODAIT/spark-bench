package com.ibm.sparktc.sparkbench.workload.sql

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.utils.SparkFuncs._
import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadConfig}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

case class SQLWorkloadConf(
                                   name: String,
                                   inputDir: Option[String],
                                   workloadResultsOutputDir: Option[String] = None,
                                   queryStr: String,
                                   cache: Boolean
                                 ) extends WorkloadConfig {

  def this(m: Map[String, Any], spark: SparkSession) = {
    this(
      verifyOrThrow(m, "name", "sql", s"Required field name does not match"),
      inputDir = Some(getOrThrow(m, "input").asInstanceOf[String]),
      workloadResultsOutputDir = getOrDefault[Option[String]](m, "workloadresultsoutputdir", None),
      getOrThrow(m, "query").asInstanceOf[String],
      getOrDefault(m, "cache", false)
    )
  }

  override def toMap(cc: AnyRef): Map[String, Any] = super.toMap(this)
}

class SQLWorkload (conf: SQLWorkloadConf, spark: SparkSession) extends Workload[SQLWorkloadConf](conf, spark) {

  def loadFromDisk(spark: SparkSession): (Long, DataFrame) = time {
    val df = load(spark, conf.inputDir.get)
    if(conf.cache) df.cache()
    df
  }

  def query(df: DataFrame, spark: SparkSession): (Long, DataFrame) = time {
    df.createOrReplaceTempView("input")
    spark.sqlContext.sql(conf.queryStr)
  }

  def save(res: DataFrame, where: String, spark: SparkSession): (Long, Unit) = time {
    writeToDisk(where, res, spark)
  }

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val timestamp = System.currentTimeMillis()
    val (loadtime, df) = loadFromDisk(spark)
    val (querytime, res) = query(df, spark)
    val (savetime, _) = conf.workloadResultsOutputDir match {
      case Some(dir) => save(res, dir, spark)
      case _ => (0L, Unit)
    }
    val total = loadtime + querytime + savetime

    val schema = StructType(
      List(
        StructField("name", StringType, nullable = false),
        StructField("timestamp", LongType, nullable = false),
        StructField("load", LongType, nullable = true),
        StructField("query", LongType, nullable = false),
        StructField("save", LongType, nullable = true),
        StructField("total_runtime", LongType, nullable = false)
      )
    )

    val timeList = spark.sparkContext.parallelize(Seq(Row("sql", timestamp, loadtime, querytime, savetime, total)))

    spark.createDataFrame(timeList, schema)
  }

}

