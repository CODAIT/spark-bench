---
layout: page
title: Spark-Submit Configuration
---

Under the hood, Spark-Bench converts users' configuration files into a series of spark-submit scripts.
The spark-submit-config section of the configuration file allows users to change the parameters of 
those spark-submits. The `class` and `jar` parameters are set by the spark-bench infrastructure. 

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Parameters](#parameters)
- [spark-submit-parallel](#spark-submit-parallel)
- [spark-home](#spark-home)
- [spark-args](#spark-args)
- [conf](#conf)
- [suites-parallel](#suites-parallel)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Parameters

| Name    | Required | Default | Description |  
| ------- | -------- | ------- | ----------- |  
| spark-submit-parallel | no | false | Controls whether spark-submits are launched in parallel. Defaults to `false` |    
| spark-home  | no | $SPARK_HOME | Path to the top level of your Spark installation |  
| spark-args  | no | master = $SPARK_MASTER_HOST | Includes master, executor-memory, and other spark-submit arguments |  
| conf        | no | -- | A series of configuration options for Spark |  
| suites-parallel | no | false | Whether the workload-suites within this spark-submit should run serially or in parallel. Defaults to `false`. |   

## spark-submit-parallel

`spark-submit-parallel` is the only parameter listed here which is set outside of the `spark-submit-config` structure.
If there are multiple spark-submits created by the config file, this boolean option determines whether they are launched 
serially or in parallel. 
This option defaults to `false` meaning the suites will run serially.

```hocon
spark-bench = {

  spark-submit-parallel = true

  spark-submit-config = {
    spark-home = //...
  }
}
```

## spark-home

`spark-home` points to the top level directory of your Spark installation. 

This parameter must either be set by the `spark-home` parameter in the configuration file
or in the environment variable `SPARK_HOME`. If both options or set, the configuration file
wins. This allows users to benchmark more than one installation of Spark within one configuration file.

## spark-args

`spark-args` contains a series of key-value pairs that reflect arguments users would normally set in their spark-submit scripts.

To read more about these options in the context of Spark, see the official documentation: <https://spark.apache.org/docs/latest/submitting-applications.html>

Probably the most important of these is `master`. Just like in a spark-submit script, users can set master to local, an IP address and port for standalone, or yarn or mesos.

```hocon
spark-args = {
  master = "local[4]" // or local[*], local[2], etc.
}
```
```hocon
spark-args = {
  master = "spark://207.184.161.138:7077" //standalone
}
```
```hocon
spark-args = {
  master = "yarn"
  deploy-mode = "cluster"
}
```
```hocon
spark-args = {
  master = "mesos://207.184.161.138:7077"
}
```
```hocon
/* 
 *  Since no master is specified here, spark-bench will look for the master 
 *  from the environment variable $SPARK_MASTER_HOST 
 */
spark-args = {
}
```

`master` is the only spark-arg that can also be set in an environment variable. If `SPARK_MASTER_HOST` and `spark-args = { master = ...` 
are both set, the configuration file option will win.

Other spark args include, but are not limited to, `deploy-mode`, `executor-memory`, `num-executors`, `total-executor-cores`, etc.

## conf

The `conf` parameter contains a series of pairs of strings representing configuration options for Spark. 
These are things that are prefaced by the `--conf` option. 

Example:
```hocon
conf = {
  "spark.dynamicAllocation.enabled" = "false"
  "spark.shuffle.service.enabled" = "false"
}
``` 

Notice that `conf` is a SERIES of options. To make a list of conf options, users must make a list of objects like so:
```hocon
conf = [
  {
    "spark.dynamicAllocation.enabled" = "false"
    "spark.shuffle.service.enabled" = "false"
  },
  {
    "spark.dynamicAllocation.enabled" = "true"
    "spark.shuffle.service.enabled" = "true"
  }
]
```
This will create two spark-submit scripts that will have the same workload-suites and other parameters, 
but the first will have `"spark.dynamicAllocation.enabled" = "false"` and `"spark.shuffle.service.enabled" = "false"`,
and the second will have `"spark.dynamicAllocation.enabled" = "true"` and `"spark.shuffle.service.enabled" = "true"`.

## suites-parallel

`suites-parallel` controls whether the workload suites within this spark-submit run serially or in parallel. 
This option defaults to `false` meaning the suites will run serially.
