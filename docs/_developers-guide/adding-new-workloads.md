---
layout: page
title: Adding New Workloads
---

The Workload is the basic atomic unit of spark-bench. 
Workloads optionally read in data from disk, do their thing, and then optionally write data to disk.



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

