/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

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
      output = None, // we don't have any output data to write to disk in the way that a SQL query would.
      str = getOrDefault[String](m, "str", "Hello, World!")
    )
}

/*
  We're going to structure the main workload as a case class that inherits from abstract class Workload.
  Input and workloadResultsOutputDir are required to be members of our case class; anything else
  depends on the workload. Here, we're taking in a string that we will be returning in our workload.
*/
case class HelloString(
                        input: Option[String] = None,
                        output: Option[String] = None,
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
