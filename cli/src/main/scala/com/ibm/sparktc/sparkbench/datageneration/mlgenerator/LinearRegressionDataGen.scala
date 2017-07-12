package com.ibm.sparktc.sparkbench.datageneration.mlgenerator

import org.apache.spark.mllib.util.LinearDataGenerator
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import com.ibm.sparktc.sparkbench.utils.{LinearRegressionDefaults, SparkBenchException}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.{getOrDefault, getOrThrow, time}
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import com.ibm.sparktc.sparkbench.workload.Workload
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}

case class LinearRegressionDataGen (
                                      name: String,
                                      numRows: Int,
                                      numCols: Int,
                                      input: Option[String] = None,
                                      output: Option[String],
                                      eps: Double,
                                      intercepts: Double,
                                      numPartitions: Int
                                   ) extends Workload {

  def this(m: Map[String, Any]) = this(
    name = getOrThrow(m, "name").asInstanceOf[String],
    numRows = getOrThrow(m, "rows").asInstanceOf[Int],
    numCols = getOrThrow(m, "cols").asInstanceOf[Int],
    output = Some(getOrThrow(m, "output").asInstanceOf[String]),
    eps = getOrDefault[Double](m, "eps", LinearRegressionDefaults.EPS),
    intercepts = getOrDefault[Double](m, "intercepts", LinearRegressionDefaults.INTERCEPTS),
    numPartitions = getOrDefault[Int](m, "partitions", LinearRegressionDefaults.NUM_OF_PARTITIONS)
  )

  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {

    val timestamp = System.currentTimeMillis()

    val (generateTime, data): (Long, RDD[LabeledPoint]) = time {
      LinearDataGenerator.generateLinearRDD(
        spark.sparkContext,
        numRows,
        numCols,
        eps,
        numPartitions,
        intercepts
      )
    }

    import spark.implicits._
    val (convertTime, dataDF) = time {
      data.toDF
    }

    val (saveTime, _) = time {
      val outputstr = output.get
      if(outputstr.endsWith(".csv")) throw SparkBenchException("LabeledPoints cannot be saved to CSV. Please try outputting to Parquet instead.")
      writeToDisk(output.get, dataDF, spark)
    }//TODO you can't output this to CSV. Parquet is fine

    val timeResultSchema = StructType(
      List(
        StructField("name", StringType, nullable = false),
        StructField("timestamp", LongType, nullable = false),
        StructField("generate", LongType, nullable = true),
        StructField("convert", LongType, nullable = true),
        StructField("save", LongType, nullable = true),
        StructField("total_runtime", LongType, nullable = false)
      )
    )

    val total = generateTime + convertTime + saveTime

    val timeList = spark.sparkContext.parallelize(Seq(Row("kmeans", timestamp, generateTime, convertTime, saveTime, total)))

    spark.createDataFrame(timeList, timeResultSchema)

  }
}
