# spark-bench Developer's Guide

Contributions welcome!

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
## Table of Contents

- [Build Structure of spark-bench](#build-structure-of-spark-bench)
- [Adding a New Data Generator](#adding-a-new-data-generator)
- [Adding a New Workload](#adding-a-new-workload)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Build Structure of spark-bench

spark-bench is a multi-project SBT build. The build is mainly defined in [build.sbt](../build.sbt) and dependencies
are defined in [Dependencies.scala](../project/Dependencies.scala).

## Adding a New Data Generator

All the data generators live in the data generator project. Let's add a new data generator called FooGenerator.

The FooGenerator is going to output however many rows and columns of the string you specify, over and over again.
Pretty useful, right?

Let's put this in `datageneration/src/main/scala/com/ibm/sparktc/sparkbench/datageneration/utilgenerators`.

```scala
package com.ibm.sparktc.sparkbench.datageneration.utilgenerators

import com.ibm.sparktc.sparkbench.datageneration.{DataGenerationConf, DataGenerator}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrDefault
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object ExampleDefaults {
  val DEFAULT_STR = "foo"
}

class ExampleGenerator(conf: DataGenerationConf, spark: SparkSession) extends DataGenerator(conf, spark) {

  import ExampleDefaults._ //this is the file we created in the `utils` project, all the wiring to get the project dependencies talking is already in place!

  val m = conf.generatorSpecific //convenience
  val str: String = getOrDefault[String](m, "str", DEFAULT_STR)

  override def generateData(spark: SparkSession): DataFrame = {

    val oneRow = Seq.fill(conf.numCols)(str).mkString(",")

    val dataset: Seq[String] = for (i <- 0 until conf.numRows) yield oneRow
    val strrdd: RDD[String] = spark.sparkContext.parallelize(dataset)
    val rdd = strrdd.map(str => str.split(","))
    val schemaString = rdd.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = false))
    val schema = StructType(fields)
    val rowRDD: RDD[Row] = rdd.map(arr => Row(arr:_*))

    spark.createDataFrame(rowRDD, schema)
  }
}
```

Notice that I included the ExampleDefaults object within this file. Sometimes it's better to store defaults that might be used in multiple places in the
`utils` project.

These defaults are defined in [Defaults.scala](../utils/src/main/scala/com/ibm/sparktc/sparkbench/utils/Defaults.scala). If we were
to refactor our generator with an external default object, we'd put it in Defaults like this:

```scala
package com.ibm.sparktc.sparkbench.utils

object KMeansDefaults {
// ...
}

object LinearRegressionDefaults {
// ...
}

object FooDefaults {
  val DEFAULT_STR = "foo"
}
```

Now create that infrastructure for taking in the argument to ScallopArgs in the `cli` project.

## Adding a New Workload

Workloads implement the `Workload` trait and override the `doWorkload` method, which accepts an optional dataframe and returns a results dataframe.  Workloads must also have companion objects implementing `WorkloadDefaults`, which store constants and construct the workload.  This custom workload must then be packaged in a JAR that must then be supplied to Spark just as any other Spark job dependency.

Let's build an example that just returns a string. That's it.  We're going to make sure that our clusters are super performant when it comes to returning strings.

Here's the code:

```scala
package com.ibm.sparktc.sparkbench.workload.custom

import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.sql.{DataFrame, SparkSession}

// Create a quick case class with a member for each field we want to return in the results.
case class HelloStringResult(
                              name: String,
                              timestamp: Long,
                              total_runtime: Long,
                              str: String
                            )

/*
  Each workload must have a companion object extending WorkloadDefaults.  Here, you define required
  constant attributes of the workload like its name, as well as any default values or constants that
  you want to use and a constructor for your workload.
 */
object HelloString extends WorkloadDefaults {
  val name = "hellostring"
  /*
    Give the WorkloadDefaults an apply method that constructs your workload  from a
    Map[String, Any]. This will be the form you receive your parameters in from the spark-bench
    infrastructure. Example:
    Map(
      "name" -> "hellostring",
      "workloadresultsoutputdir" -> None,
      "str" -> "Hi I'm an Example"
    )

    Keep in mind that the keys in your map have been toLowerCase()'d for consistency.
  */
  def apply(m: Map[String, Any]) =
    new HelloString(input = None, // we don't need to read any input data from disk
      workloadResultsOutputDir = None, // we don't have any output data to write to disk in the way that a SQL query would.
      str = getOrDefault(m, "str", "Hello, World!")
    )
}

/*
  We're going to structure the main workload as a case class that inherits from abstract class Workload.
  Input and workloadResultsOutputDir are required to be members of our case class; anything else
  depends on the workload. Here, we're taking in a string that we will be returning in our workload.
*/
case class HelloString(
                        input: Option[String] = None,
                        workloadResultsOutputDir: Option[String] = None,
                        str: String
                      ) extends Workload {

  /*
    doWorkload is an abstract method from Workload. It may or may not take input data, and it will
      output a one-row DataFrame made from the results case class we defined above.
  */
  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    // Every workload returns a timestamp from the start of its doWorkload() method
    val timestamp = System.currentTimeMillis()
    /*
      The time {} function returns a tuple of the start-to-finish time of whatever function
      or block you are measuring, and the results of that code. Here, it's going to return a tuple
      of the time and the string that's being returned. If we don't care about the results, we can assign it to _.
      Here I've assigned the results to returnedString just for clarity.
    */
    val (t, returnedString) = time {
      str
    }

    /*
      And now we have everything we need to construct our results case class and create a DataFrame!
    */
    spark.createDataFrame(Seq(HelloStringResult(HelloString.name, timestamp, t, returnedString)))
  }
}
```

Creating a JAR of this single file using `sbt package` should be sufficient to produce a JAR that can be used with spark-bench.  To configure spark-bench to use this new custom workload, add a workload with name "custom" and the key "class" set to the fully qualified name of the workload class.  Any other parameters can be provided as usual.  For example, to use our `HelloString` workload:

```
workloads = [
  {
    name = "custom"
    class = "com.ibm.sparktc.sparkbench.workload.custom.HelloString"
    str = ["Hello", "Hi"]
  }
]
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
