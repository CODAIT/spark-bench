---
layout: page
title: Using Custom Workloads
---

Workloads implement the `Workload` trait and override the `doWorkload` method, which accepts an optional dataframe and 
returns a results dataframe.  
Workloads must also have companion objects implementing `WorkloadDefaults`, which store constants and construct the workload.  
This custom workload must then be packaged in a JAR that must then be supplied to Spark just as any other Spark job dependency.

Let's build an example that just returns a string. That's it.  
We're going to make sure that our clusters are super performant when it comes to returning strings.

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
