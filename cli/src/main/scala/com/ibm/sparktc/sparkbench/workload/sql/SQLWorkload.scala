package com.ibm.sparktc.sparkbench.workload.sql

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.utils.SparkFuncs._
import com.ibm.sparktc.sparkbench.workload.Workload
import org.apache.spark.sql.{DataFrame, SparkSession}

case class SQLWorkloadResult(
                            name: String,
                            timestamp: Long,
                            loadTime: Long,
                            queryTime: Long,
                            saveTime: Long = 0L,
                            total_Runtime: Long
                            )

case class SQLWorkload (name: String,
                        input: Option[String],
                        output: Option[String] = None,
                        queryStr: String,
                        cache: Boolean) extends Workload {

  def this(m: Map[String, Any]) =
  this(
    name = verifyOrThrow(m, "name", "sql", "Incorrect or missing workload name."),
    input = m.get("input").map(_.asInstanceOf[String]),
    output = m.get("workloadresultsoutputdir").map(_.asInstanceOf[String]),
    queryStr = getOrThrow(m, "query").asInstanceOf[String],
    cache = getOrDefault(m, "cache", false)
  )

  def loadFromDisk(spark: SparkSession): (Long, DataFrame) = time {
    val df = load(spark, input.get)
    if(cache) df.cache()
    df
  }

  def query(df: DataFrame, spark: SparkSession): (Long, DataFrame) = time {
    df.createOrReplaceTempView("input")
    spark.sqlContext.sql(queryStr)
  }

  def save(res: DataFrame, where: String, spark: SparkSession): (Long, Unit) = time {
    writeToDisk(where, res, spark)
  }

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val timestamp = System.currentTimeMillis()
    val (loadtime, df) = loadFromDisk(spark)
    val (querytime, res) = query(df, spark)
    val (savetime, _) = output match {
      case Some(dir) => save(res, dir, spark)
      case _ => (0L, Unit)
    }
    val total = loadtime + querytime + savetime

    spark.createDataFrame(Seq(
      SQLWorkloadResult(
        "sql",
        timestamp,
        loadtime,
        querytime,
        savetime,
        total
      )
    ))
  }

}

