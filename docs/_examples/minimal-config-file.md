---
layout: page
title: Minimal Configuration File
---

This is an example of a really minimal but complete configuration file. This one just runs one tiny instance of the SparkPi workload.

## Setting spark-home and master
Spark-Bench must know the location of your Spark installation and must have master specified. 
You can specify these variables in your config file or in your environment

## Spark variables set in config file
```hocon
spark-bench = {
  spark-submit-config = [{
    spark-home = "/path/to/your/spark/installation"
    spark-args = {
      master = "XXXXXX" // replace with local, yarn, whatever
    }
    workload-suites = [
      {
        descr = "One run of SparkPi and that's it!"
        benchmark-output = "console"
        workloads = [
          {
            name = "sparkpi"
            slices = 10
          }
        ]
      }
    ]
  }]
}
````

## Spark variables set in environment
```bash
export SPARK_HOME=/path/to/your/spark/installation
export SPARK_MASTER_HOST=XXXX # replace with local, yarn, whatever
```

```hocon
// spark-home and master will be picked up from the environment
spark-bench = {
  spark-submit-config = [{
    workload-suites = [
      {
        descr = "One run of SparkPi and that's it!"
        benchmark-output = "console"
        workloads = [
          {
            name = "sparkpi"
            slices = 10
          }
        ]
      }
    ]
  }]
}
````
