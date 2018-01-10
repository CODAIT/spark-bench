---
layout: page
title: Spark-Submit Configuration
---

Spark-Bench will take a configuration file and launch the jobs described on a Spark cluster.
By default jobs are launched through access to `bin/spark-submit`. As of Spark-Bench version 0.3.0,
users can also launch jobs through the Livy REST API.

*NEW* for Spark-Bench 0.3.0: Livy Support



Under the hood, Spark-Bench converts users' configuration files into a series of spark-submit scripts.
The spark-submit-config section of the configuration file allows users to change the parameters of 
those spark-submits. The `class` and `jar` parameters are set by the spark-bench infrastructure. 

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Common Parameters](#common-parameters)
  - [spark-submit-parallel](#spark-submit-parallel)
  - [spark-args](#spark-args)
  - [conf](#conf)
  - [suites-parallel](#suites-parallel)
  - [spark-bench-jar](#spark-bench-jar)
- [Launching Jobs Through Spark-Submit](#launching-jobs-through-spark-submit)
  - [Parameters](#parameters)
  - [spark-home](#spark-home)
  - [spark-args.master](#spark-argsmaster)
- [Launching Jobs Through Livy REST API](#launching-jobs-through-livy-rest-api)
  - [Parameters](#parameters-1)
  - [livy.url](#livyurl)
  - [livy.poll-seconds](#livypoll-seconds)
  - [spark-args In the Context of Livy Submission](#spark-args-in-the-context-of-livy-submission)
  - [conf In the Context of Livy Submission](#conf-in-the-context-of-livy-submission)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Common Parameters

| Name    | Required | Default | Description |  
| ------- | -------- | ------- | ----------- |  
| spark-submit-parallel | no | false | Controls whether spark-submits are launched in parallel. Defaults to `false` |    
| spark-args  | no | -- | Includes master, executor-memory, and other spark-submit arguments |  
| conf        | no | -- | A series of configuration options for Spark |  
| suites-parallel | no | false | Whether the workload-suites within this spark-submit should run serially or in parallel. Defaults to `false`. |   
| spark-bench-jar | no | distribution lib/ directory or repo target/assembly/ directory | Path to the Spark-Bench jar that is submitted by the spark-bench-launch jar |   

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

## spark-args

`spark-args` contains a series of key-value pairs that reflect arguments users would normally set in their spark-submit scripts.

To read more about these options in the context of Spark, see the official documentation: <https://spark.apache.org/docs/latest/submitting-applications.html>

Probably the most important of these is `master` which is discussed in detail below.

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

## spark-bench-jar

This option lets users specify a full path (local, hdfs, object storage, etc.) to their Spark-Bench jar.

spark-bench-launch will look for the accompanying Spark-Bench jar in the following places and in this order.

1. The location specified by the user in the config file through `spark-bench-jar`
2. In the `lib/` file of the distribution (distributions can be downloaded directly from Github Releases or creating from the repo by running `sbt dist`)
3. In the `target/assembly/` directories of the repo (where jars created by `sbt assembly` are stored)

For many cases it is sufficient to rely on spark-bench-launch to find the jars using options 2 or 3 above. However, 
for launching through Livy or when launching the spark-submit on Yarn using cluster-mode, or any number of other cases,
you may need to have the spark-bench jar stored in HDFS or elsewhere, and in this case you can provide a full path to that HDFS, S3, or other URL.

# Launching Jobs Through Spark-Submit

## Parameters

| Name    | Required | Default | Description |  
| ------- | -------- | ------- | ----------- |  
| spark-home  | no | $SPARK_HOME | Path to the top level of your Spark installation |  
| spark-args.master  | no | $SPARK_MASTER_HOST | The same as --master for a spark-submit script |  

## spark-home

`spark-home` points to the top level directory of your Spark installation. 

This parameter must either be set by the `spark-home` parameter in the configuration file
or in the environment variable `SPARK_HOME`. If both options or set, the configuration file
wins. This allows users to benchmark more than one installation of Spark within one configuration file.

## spark-args.master

when launching jobs through bin/spark-submit, users must specify a master to use. This can be done inside of the `spark-args` block
or through the environment variable `$SPARK_MASTER_HOST`

Just like in a spark-submit script, users can set master to local, an IP address and port for standalone, or yarn or mesos.

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

# Launching Jobs Through Livy REST API

*New for Spark-Bench 0.3.0*

It is recommended that users familiarize themselves with simple manual usage of Livy's batch API before using the Spark-Bench capabilities.

Documentation for Livy is available through the project website: https://livy.incubator.apache.org/

## Parameters

| Name    | Required | Default | Description |  
| ------- | -------- | ------- | ----------- |  
| livy.url              | yes | -- | full URL for livy server |
| livy.poll-seconds     | no  | 5  | polling interval after job submission |   

## livy.url

The full URL (and port, if necessary) of the Livy server.

## livy.poll-seconds

After jobs are launched on the Livy server, Spark-Bench must poll the status API to determine when the job is finished
and subsequently fetch results. This parameter determines the length of the polling interval, defaulting to 5 seconds.

## spark-args In the Context of Livy Submission

As above, this structure contains a series of key-value pairs very similar to those in `spark-args` above, but with the expectation
that the keys match those available to users in Livy's documentation: https://livy.incubator.apache.org/docs/latest/rest-api.html#post-batches

The `files` parameter in the request body is populated with the `spark-bench-jar` option as described above.


## conf In the Context of Livy Submission

Depending on the details of your Livy server setup, certain conf options may not be used in the job launch.
