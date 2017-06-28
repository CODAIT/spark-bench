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

The spark-bench developers are actively working on features that will enable users to dynamically load their own workloads built against the Workload abstract class.

In the meantime, users will need to add their workloads into the codebase of spark-bench itself. Let's build an example that just returns a string. That's it.
We're going to make sure that our clusters are super performant when it comes to returning strings.

Here's the code:

```scala
package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.workload.Workload
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import org.apache.spark.sql.{DataFrame,  SparkSession}

// Create a case class with a member for each field we want to return in the results.
case class HelloStringResult(
                        name: String,
                        timestamp: Long,
                        total_runtime: Long,
                        str: String
                      )
/*
  We're going to structure the main workload as a case class that inherits from abstract class Workload.
  Name, input, and workloadResultsOutputDir are required to be members of our case class, anything else
  depends on the workload. Here, we're taking in a string that we will be returning in our workload.
*/
case class HelloString(
                  name: String,
                  input: Option[String] = None,
                  workloadResultsOutputDir: Option[String] = None,
                  str: String
                ) extends Workload {

/*
  Override the constructor for your case class to take in a Map[String, Any]. This will 
  be the form you receive your parameters in from the spark-bench infrastructure. Example:
  Map(
    "name" -> "hellostring",
    "workloadresultsoutputdir" -> None,
    "str" -> "Hi I'm an Example"
  )
  
  Keep in mind that the keys in your map have been toLowerCase()'d for consistency.
*/
  def this(m: Map[String, Any]) =
    this(name = getOrDefault(m, "name", "hellostring"),
      input = m.get("input").map(_.asInstanceOf[String]),
      workloadResultsOutputDir = None,
      str = getOrDefault(m, "str", "Hello, World!")
    )

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
    spark.createDataFrame(Seq(HelloStringResult(name, timestamp, t, returnedString)))
  }
}

```
Now that we have our amazing workload written, we're reading to tie it into the rest of the framework.

In [ConfigCreator](../workloads/src/main/scala/com/ibm/sparktc/sparkbench/workload/ConfigCreator.scala)
add your workload to the match statement in mapToConf().

```scala
  def mapToConf(m: Map[String, Any]): Workload = {
    val name = getOrThrow(m, "name").asInstanceOf[String].toLowerCase
    name match {
      case "timedsleep" => new PartitionAndSleepWorkload(m)
      case "kmeans" => new KMeansWorkload(m)
      case "lr-bml" => new LogisticRegressionWorkload(m)
      case "cachetest" => new CacheTest(m)
      case "sql" => new SQLWorkload(m)
      case "sleep" => new Sleep(m)
      case "sparkpi" => new SparkPi(m)
      case "hellostring" => new HelloString(m) // <--- TA-DA!!
      case _ => throw SparkBenchException(s"Unrecognized or implemented workload name: $name")
    }
  }
```

Now your workload will work through a config file! Let's also make sure it works through
the CLI.

In the cli project, add your arguments to [ScallopArgs](../cli/src/main/scala/com/ibm/sparktc/sparkbench/cli/ScallopArgs.scala).
Scallop is weird and cool and weird, you can read more about it here: <https://github.com/scallop/scallop/wiki>
We're going to make our new arguments a subclass of SuiteArgs, which means that name, input, and workloadResultsOutput
are already defined for us, we just have to add any extra stuff for our particular workload.

Finally, we add our subcommand to the workload subcommand. Yeah, I know, Scallop is weird and cool and weird.

```scala
    // STRING RETURNER
    val helloString = new SuiteArgs("hellostring") {
      val str = opt[List[String]](short = 's', default = Some(List("Hello, World!")), required = true)
    }
    addSubcommand(helloString)
```

And that's it! Now your new workload is good to go!