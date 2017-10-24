---
layout: page
title: Using Custom Workloads
---

This guide will walk you through making custom workloads for Spark-Bench.

The WordGenerator example project shows how to compile against local Spark-Bench jars
and create a custom workload: <https://github.com/ecurtin/spark-bench-custom-workload>

Workloads implement the `Workload` abstract class and override the `doWorkload` method, which accepts an optional dataframe and 
returns a results dataframe.  

Custom workloads must also have companion objects implementing `WorkloadDefaults`, which store constants and construct the workload.  
This custom workload must then be packaged in a JAR that must then be supplied to Spark just as any other Spark job dependency.

Let's build an example that generates a dataset with just one word repeated over and over.

When we're done, we'll be able to use a configuration block like this in our Spark-Bench config file:
```hocon
  {
    name = "custom"
    class = "com.example.WordGenerator"
    output = "console"
    rows = 10
    cols = 3
    word = "Cool stuff!!"
  }
```
which will produce output like this:
```text
+------------+------------+------------+
|           0|           1|           2|
+------------+------------+------------+
|Cool stuff!!|Cool stuff!!|Cool stuff!!|
|Cool stuff!!|Cool stuff!!|Cool stuff!!|
|Cool stuff!!|Cool stuff!!|Cool stuff!!|
|Cool stuff!!|Cool stuff!!|Cool stuff!!|
|Cool stuff!!|Cool stuff!!|Cool stuff!!|
|Cool stuff!!|Cool stuff!!|Cool stuff!!|
|Cool stuff!!|Cool stuff!!|Cool stuff!!|
|Cool stuff!!|Cool stuff!!|Cool stuff!!|
|Cool stuff!!|Cool stuff!!|Cool stuff!!|
|Cool stuff!!|Cool stuff!!|Cool stuff!!|
+------------+------------+------------+
```

Here's the code:

```scala
package com.example

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

// Create a quick case class with a member for each field we want to return in the results.
case class WordGeneratorResult(
                                name: String,
                                timestamp: Long,
                                generate_time: Long,
                                convert_time: Long,
                                save_time: Long,
                                total_runtime: Long,
                                word: String
                              )


/*
  Each workload must have a companion object extending WorkloadDefaults.  Here, you define required
  constant attributes of the workload like its name, as well as any default values or constants that
  you want to use and a constructor for your workload.
 */
object WordGenerator extends WorkloadDefaults {
  val name = "word-generator"

  /*
    Give the WorkloadDefaults an apply method that constructs your workload  from a
    Map[String, Any]. This will be the form you receive your parameters in from the spark-bench
    infrastructure. Example:
    Map(
      "name" -> "word-generator",
      "output" -> "/tmp/one-word-over-and-over.csv",
      "word" -> "Cool"
    )
    Keep in mind that the keys in your map have been toLowerCase()'d for consistency.
  */

  override def apply(m: Map[String, Any]) = WordGenerator(
    numRows = getOrThrow(m, "rows").asInstanceOf[Int],
    numCols = getOrThrow(m, "cols").asInstanceOf[Int],
    output = Some(getOrThrow(m, "output").asInstanceOf[String]),
    word = getOrThrow(m, "word").asInstanceOf[String]
  )
}


case class WordGenerator(
                          numRows: Int,
                          numCols: Int,
                          input: Option[String] = None,
                          output: Option[String],
                          word: String
                        ) extends Workload {
  /*
    doWorkload() is an abstract method from Workload. It optionally takes input data, and it will
    output a one-row DataFrame made from the results case class we defined above.
  */
  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    // Every workload returns a timestamp from the start of its doWorkload() method
    val timestamp = System.currentTimeMillis()

    /*
      The time {} function returns a tuple of the start-to-finish time of whatever function
      or block you are measuring, and the results of that code. Here, it's going to return a tuple
      of the time and the string that's being returned.
      If we don't care about the results, we can assign it to _.
    */
    val (generateTime, strrdd): (Long, RDD[String]) = time {
      val oneRow = Seq.fill(numCols)(word).mkString(",")
      val dataset: Seq[String] = for (i <- 0 until numRows) yield oneRow
      spark.sparkContext.parallelize(dataset)
    }

    val (convertTime, dataDF) = time {
      val rdd = strrdd.map(str => str.split(","))
      val schemaString = rdd.first().indices.map(_.toString).mkString(" ")
      val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = false))
      val schema = StructType(fields)
      val rowRDD: RDD[Row] = rdd.map(arr => Row(arr: _*))
      spark.createDataFrame(rowRDD, schema)
    }

    val (saveTime, _) = time { writeToDisk(output.get, dataDF, spark) }

    val totalTime = generateTime + convertTime + saveTime

    /* 
     * NOTE: The dataframe we're returning is NOT the generated datset. It's the one-line
     * benchmark results of how long it took to generate the dataset
     */
    spark.createDataFrame(Seq(WordGeneratorResult("word-generator", timestamp, generateTime, convertTime, saveTime, totalTime, word)))

  }
}
```

You can compile your custom workload using SBT against your local spark-bench jars, like this:
```scala
lazy val sparkBenchPath = "/ABSOLUTE/PATH/TO/YOUR/SPARK/BENCH/INSTALLATION/lib/"
lazy val sparkVersion = "2.1.1"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.11.8",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "WordGenerator",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core"  % sparkVersion % "provided",
      "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql"   % sparkVersion % "provided"
    ),
    unmanagedBase := new java.io.File(sparkBenchPath)
)

```

The most complicated part of the process may be getting Spark to properly handle the new JAR.  This must be done with the `spark-args` key in the configuration file, which assembles the arguments and passes them as-is to `spark-submit`.  For runs on a single machine, simply setting `driver-class-path` may be sufficient.  For example, if my custom workload is located at `/home/drwho/code/spark-bench/spark-launch/src/test/resources/jars/HelloWorld.jar`, `spark-args` may look something like:

```
spark-args = {
  master = "local[*]"
  driver-class-path = "/home/drwho/code/spark-bench/spark-launch/src/test/resources/jars/*"
}
```

If spark-bench is running distributed and this does not work, try setting `jars` to the full path to your JAR instead.

And that's it! Now your new workload is good to go!
