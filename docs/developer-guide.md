<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Build Structure of spark-bench](#build-structure-of-spark-bench)
- [Adding a New Data Generator](#adding-a-new-data-generator)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

#spark-bench Developer's Guide

Contributions welcome!

## Build Structure of spark-bench

spark-bench is a multi-project SBT build. The build is mainly defined in [build.sbt](../build.sbt) and dependencies
are defined in [Dependencies.scala](../project/Dependecies.scala).

## Adding a New Data Generator

All the data generators live in the data generator project. Let's add a new data generator called FooGenerator.

The FooGenerator is going to output however many rows and columns of the string you specify, over and over again.
Pretty useful, right?

We probably want a default string for the FooGenerator to output. Let's make a defaults file. This should
go in the `utils` project because defaults may need to be accessed in multiple projects.

Defaults are defined in [Defaults.scala](../utils/src/main/scala/com/ibm/sparktc/sparkbench/utils/Defaults.scala)

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

Now let's actually write our generator! Let's put this in `datageneration/src/main/scala/com/ibm/sparktc/sparkbench/datageneration/utilgenerators`.

```scala
package com.ibm.sparktc.sparkbench.datageneration.utilgenerators

import com.ibm.sparktc.sparkbench.datageneration.{DataGenerationConf, DataGenerator}
import com.ibm.sparktc.sparkbench.utils.FooDefaults
import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrDefault

import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

class FooDataGen(conf: DataGenerationConf, spark: SparkSession) extends DataGenerator(conf, spark) {

  import FooDefaults._ //this is the file we created in the `utils` project, all the wiring to get the project dependencies talking is already in place!

  val m = conf.generatorSpecific //convenience
  val str: Int = getOrDefault[Int](m, "str", DEFAULT_STR)

  override def generateData(spark: SparkSession): DataFrame = {

    val oneRow = (0..conf.numCols).map(i => str).toSeq
    val dataset = (0..conf.numRows).map(i => oneRow).toSeq
    spark.sparkContext.parallelize(dataset)

    val schemaString = data.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = false))
    val schema = StructType(fields)
    val rowRDD = data.map(arr => Row(arr:_*))

    spark.createDataFrame(rowRDD, schema)
  }
}
```

Now create that infrastructure for taking in the argument to ScallopArgs in the `cli` project.

## Adding a New Workload

This section needs more fleshing out.

The TL;DR is that while there are plans in the roadmap to add the ability to use custom workloads on the fly, we're not there yet. 
In the meantime, you have to add your workload inside of the `workloads` project just like the existing ones.
