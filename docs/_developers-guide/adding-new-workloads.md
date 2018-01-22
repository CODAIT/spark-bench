---
layout: page
title: Adding New Workloads
---



## Overview

The Workload is the basic atomic unit of spark-bench. 
Workloads optionally read in data from disk, do their thing, and then optionally write data to disk.

In this example we're going to create a new data generation workload. Data generators are treated just like any other workload.
They are composed of an object that extends WorkloadDefaults and transforms the parameters from the config file into parameters
for the Workload case class. The Workload case class extends Workload and must override the method doWorkload() which returns
a one-row DataFrame of the benchmark results of that workload.

In this example we're going to create a data generator called ExampleGenerator.
It's going to output however many rows and columns of the string you specify, over and over again.
Pretty useful, right? Doesn't everybody want thousands of copies of the word "foo"? Maybe "bar" is more your style.

## Parameters From The Config File

For our ExampleGenerator we're going to have just a handful of parameters. We want users to be
able to add the ExampleGenerator to their config file like this:

```hocon
// This is within the nested structure of spark-bench = { spark-submit-config = { workload-suites = [{ ... }]}
{
  name = "example-generator"
  rows = 1000
  cols = 24
  output = "hdfs:///tmp/example-data.parquet"
  str = "Hello Example Generator!"
}
```

The config file is parsed by a series of classes that will crossjoin the parameters as necessary
and parse the parameters for each workload blindly into a `Map[String, Any]` like this:

```scala
Map(
  "name" -> "example-generator",
  "rows" -> 1000,
  "cols" -> 24,
  "output" -> "hdfs:///tmp/example-data.parquet",
  "str" -> "Hello Example Generator!"
)
```

We will need to parse this map into the parameters for our Workload by the object that extends WorkloadDefaults.
But first, let's talk about the results case class.

## Case Class for the Benchmark Results

The first thing we're going to do is create a case class for the benchmark results DataFrame that we'll be returning.
There's a couple different valid ways to construct a DataFrame from scratch. This is just one
that happens to fit the goals of what we're doing and look pretty clean.

```scala
/**
  * This is a case class that represents the columns for the one-row DataFrame that
  * we'll be returning at the end of generating the data.
  */
case class ExampleGeneratorResult(
                                   name: String,
                                   rows: Int,
                                   cols: Int,
                                   str: String,
                                   start_time: Long,
                                   create_time: Long,
                                   transform_time: Long,
                                   save_time: Long,
                                   total_runtime: Long
                                 )
```

## Object to Parse Parameters From the Config File

Next we'll go back to what we were talking about before and create an object 
that will parse the `Map[String, Any]` given to us by the config file interpretation structure. 
This object must extend WorkloadDefaults and it must have a string value for `name`.

This object will need to validate the parameters from the config file and substitute
any defaults for missing values.

The case class it returns is the case class we'll define in the next step.

```scala
/**
  * The ExampleDefaults object unzips the Map[String, Any] passed to it from the
  * config parsing structure. It validates the parameters, substitutes any defaults
  * as necessary, and then creates an instance of the ExampleGenerator case class.
  */
object ExampleDefaults extends WorkloadDefaults {
  val DEFAULT_STR = "foo"
  val name = "example-generator"

  override def apply(m: Map[String, Any]): ExampleGenerator =
    ExampleGenerator(
      numRows = getOrThrow(m, "rows").asInstanceOf[Int],
      numCols = getOrThrow(m, "cols").asInstanceOf[Int],
      output = Some(getOrThrow(m, "output").asInstanceOf[String]),
      str = m.getOrElse("str", DEFAULT_STR).asInstanceOf[String]
    )
}
```

## Case Class For The Actual Workload

Now we're going to finally get to doing the workload. We're going to time each stage of what we're
doing and then put together a one-row DataFrame of those timing results.

```scala
/**
  * The ExampleGenerator case class has as constructor arguments every parameter
  * necessary to run the workload. Some arguments, like input and output, are
  * must be included according to the definition of Workload, which this case class is
  * extending. Because this is a data generator, it doesn't take in any data and we
  * can safely set the input parameter to be None by default.
  *
  * Classes that extend Workload must implement the method doWorkload() which
  * optionally takes in data (again, because this is a data generator we don't need to take
  * in any data), and returns a Dataframe NOT of the results of the workload itself,
  * but of the BENCHMARK results. The results of the workload itself are written out to
  * the location specified in the output parameter.
  */
case class ExampleGenerator(
                             numRows: Int,
                             numCols: Int,
                             input: Option[String] = None,
                             output: Option[String],
                             str: String
                           ) extends Workload {

  private def createData(spark: SparkSession) = {
    // Create one row of our amazing data
    val oneRow = Seq.fill(numCols)(str).mkString(",")

    // Turn it into an RDD of size numRows x numCols
    val data: Seq[String] = for (i <- 0 until numRows) yield oneRow
    val strrdd: RDD[String] = spark.sparkContext.parallelize(data)
    strrdd.map(str => str.split(","))
  }

  private def createDataFrame(rdd: RDD[Array[String]], spark: SparkSession): DataFrame = {
    // In order to make a dataframe we'll need column names and a schema.
    // This just uses the column index as the name for each column.
    val schemaString = rdd.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = false))
    val schema = StructType(fields)
    val rowRDD: RDD[Row] = rdd.map(arr => Row(arr:_*))

    // Now we have our dataframe that we'll write to the location in output.
    spark.createDataFrame(rowRDD, schema)
  }
  
  /**
    * This is where we're doing the actual work of the workload
    */
  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val startTime = System.currentTimeMillis
    // Generate the data and time all the different stages
    val (createTime, rdd) = time(createData(spark))
    val (transformTime, df) = time(createDataFrame(rdd, spark))
    val (saveTime, _) = time{ writeToDisk(output.get, df, spark) }

    // And now let's use that case class from above to create the one-row dataframe of our benchmark results
    spark.createDataFrame(
      Seq(
        ExampleGeneratorResult(
          name = ExampleDefaults.name,
          rows = numRows,
          cols = numCols,
          str = str,
          start_time = startTime,
          create_time = createTime,
          transform_time = transformTime,
          save_time = saveTime,
          total_runtime = createTime + transformTime + saveTime
        ))
    )
  }
}
```

## Putting It All Together

This is what our file ExampleGenerator.scala will look like put together with imports and everything.

```scala
package com.ibm.sparktc.sparkbench.datageneration

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

/**
  * This is a case class that represents the columns for the one-row DataFrame that
  * we'll be returning at the end of generating the data.
  */
case class ExampleGeneratorResult(
                                   name: String,
                                   rows: Int,
                                   cols: Int,
                                   str: String,
                                   start_time: Long,
                                   create_time: Long,
                                   transform_time: Long,
                                   save_time: Long,
                                   total_runtime: Long
                                 )

/**
  * The ExampleDefaults object unzips the Map[String, Any] passed to it from the
  * config parsing structure. It validates the parameters, substitutes any defaults
  * as necessary, and then creates an instance of the ExampleGenerator case class.
  */
object ExampleDefaults extends WorkloadDefaults {
  val DEFAULT_STR = "foo"
  val name = "example-generator"

  override def apply(m: Map[String, Any]): ExampleGenerator =
    ExampleGenerator(
      numRows = getOrThrow(m, "rows").asInstanceOf[Int],
      numCols = getOrThrow(m, "cols").asInstanceOf[Int],
      output = Some(getOrThrow(m, "output").asInstanceOf[String]),
      str = m.getOrElse("str", DEFAULT_STR).asInstanceOf[String]
    )
}

/**
  * The ExampleGenerator case class has as constructor arguments every parameter
  * necessary to run the workload. Some arguments, like input and output, are
  * must be included according to the definition of Workload, which this case class is
  * extending. Because this is a data generator, it doesn't take in any data and we
  * can safely set the input parameter to be None by default.
  *
  * Classes that extend Workload must implement the method doWorkload() which
  * optionally takes in data (again, because this is a data generator we don't need to take
  * in any data), and returns a Dataframe NOT of the results of the workload itself,
  * but of the BENCHMARK results. The results of the workload itself are written out to
  * the location specified in the output parameter.
  */
case class ExampleGenerator(
                             numRows: Int,
                             numCols: Int,
                             input: Option[String] = None,
                             output: Option[String],
                             str: String
                           ) extends Workload {

  private def createData(spark: SparkSession) = {
    // Create one row of our amazing data
    val oneRow = Seq.fill(numCols)(str).mkString(",")

    // Turn it into an RDD of size numRows x numCols
    val data: Seq[String] = for (i <- 0 until numRows) yield oneRow
    val strrdd: RDD[String] = spark.sparkContext.parallelize(data)
    strrdd.map(str => str.split(","))
  }

  private def createDataFrame(rdd: RDD[Array[String]], spark: SparkSession): DataFrame = {
    // In order to make a dataframe we'll need column names and a schema.
    // This just uses the column index as the name for each column.
    val schemaString = rdd.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = false))
    val schema = StructType(fields)
    val rowRDD: RDD[Row] = rdd.map(arr => Row(arr:_*))

    // Now we have our dataframe that we'll write to the location in output.
    spark.createDataFrame(rowRDD, schema)
  }

  /**
    * This is where we're doing the actual work of the workload
    */
  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val startTime = System.currentTimeMillis
    // Generate the data and time all the different stages
    val (createTime, rdd) = time(createData(spark))
    val (transformTime, df) = time(createDataFrame(rdd, spark))
    val (saveTime, _) = time{ writeToDisk(output.get, df, spark) }

    // And now let's use that case class from above to create the one-row dataframe of our benchmark results
    spark.createDataFrame(
      Seq(
        ExampleGeneratorResult(
          name = ExampleDefaults.name,
          rows = numRows,
          cols = numCols,
          str = str,
          start_time = startTime,
          create_time = createTime,
          transform_time = transformTime,
          save_time = saveTime,
          total_runtime = createTime + transformTime + saveTime
        ))
    )
  }
}
```

## Tying Our Workload Into The The Project

Remember the `name` parameter we had to create in our ExampleGeneratorDefaults object?
We're going to use that to tie in our workload to the rest of the Spark-Bench infrastructure.

In the `cli` project there is an object called ConfigCreator with a sequence of WorkloadDefaults.
We simply need to add our newly created ExampleGeneratorDefaults object to this list.

```scala
object ConfigCreator {

  private val workloads: Map[String, WorkloadDefaults] = Set(
    PartitionAndSleepWorkload,
    KMeansWorkload,
    LogisticRegressionWorkload,
    CacheTest,
    SQLWorkload,
    Sleep,
    SparkPi,
    KMeansDataGen,
    LinearRegressionDataGen,
    LinearRegressionWorkload,
    GraphDataGen,
    // And here we add our object that we just created:
    ExampleDefaults
  ).map(wk => wk.name -> wk).toMap
  
  // rest of the object, we don't need to worry about what's down here
```

And that's it! That's all we have to do to create a new Workload and tie it into the structure of Spark-Bench.

## Unit Testing

Workloads also need to have unit tests. There's many different ways to do this. It's recommended that you go
look around in the test files to see some examples and get some inspiration.

## Happy Coding!