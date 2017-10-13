---
layout: page
title: CSV vs. Parquet
---

This is an experimental setup for benchmarking the performance of some 
simple SQL queries over the same dataset store in CSV and Parquet.

In this case, only one spark submit is needed. Within that spark-submit, several workload-suites get run serially.

The first workload suite first generates data using `data-generation-kmeans`. Then a second workload in that suite
picks up that dataset and writes it out as Parquet.

In the second dataset, four different workloads are setup in one workload block. Each of them runs a sql query.
Under the hood, spark-bench will take the two parameter lists and cross-join them to create, in this instance, four
workload configurations.

```hocon
spark-bench = {
  spark-submit-config = [{
    spark-home = "XXXXXXX" // PATH TO YOUR SPARK INSTALLATION
    spark-args = {
      master = "XXXXXXX" // FILL IN YOUR MASTER HERE
      executor-memory = "XXXXXXX" // FILL IN YOUR EXECUTOR MEMORY
    }
    conf = {
      // Any configuration you need for your setup goes here, like:
      // "spark.dynamicAllocation.enabled" = "false"
    }
    suites-parallel = false
    workload-suites = [
      {
        descr = "Generate a dataset, then take that same dataset and write it out to Parquet format"
        benchmark-output = "hdfs:///tmp/csv-vs-parquet/results-data-gen.csv"
        // We need to generate the dataset first through the data generator, then we take that dataset and convert it to Parquet.
        parallel = false
        workloads = [
          {
            name = "data-generation-kmeans"
            rows = 10000000
            cols = 24
            output = "hdfs:///tmp/csv-vs-parquet/kmeans-data.csv"
          },
          {
            name = "sql"
            query = "select * from input"
            input = "hdfs:///tmp/csv-vs-parquet/kmeans-data.csv"
            output = "hdfs:///tmp/csv-vs-parquet/kmeans-data.parquet"
          }
        ]
      },
      {
        descr = "Run two different SQL queries over the dataset in two different formats"
        benchmark-output = "hdfs:///tmp/csv-vs-parquet/results-sql.csv"
        parallel = false
        repeat = 10
        workloads = [
          {
            name = "sql"
            input = ["hdfs:///tmp/csv-vs-parquet/kmeans-data.csv", "hdfs:///tmp/csv-vs-parquet/kmeans-data.parquet"]
            query = ["select * from input", "select `0`, `22` from input where `0` < -0.9"]
            cache = false
          }
        ]
      }
    ]
  }]
}
```