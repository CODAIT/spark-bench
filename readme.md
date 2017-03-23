# spark-bench
## Benchmark Suite for Apache Spark

[![Build Status](https://travis-ci.org/ecurtin/spark-bench.svg?branch=master)](https://travis-ci.org/ecurtin/spark-bench)
[![codecov](https://codecov.io/gh/ecurtin/spark-bench/branch/master/graph/badge.svg)](https://codecov.io/gh/ecurtin/spark-bench)


### Current Spark and spark-bench version: 2.1.0

## History

`spark-bench` began as a project led by researchers at IBM to produce benchmarks
for many different, mostly ML-focused, workloads. 

In 2017, `spark-bench` was re-written to incorporate a command line interface 
and to greatly extend the capabilities of the project while still retaining the original workloads and functionality.

## Installation

While the project is in rewrite mode without an official release, the best way to install spark-bench is to clone 
 this repo to your local system and build using `sbt assembly`.

## Usage

The rewrite is currently in MVP stage and only supports KMeans data generation and workload.

See `bin/kmeans-example.sh` and run it from the spark-bench directory to see it in action.

`spark-bench` or `spark-bench help` shows the help menu.

```
      --help   Show help message

Subcommand: generate-data
  -c, --num-cols  <arg>
  -r, --num-rows  <arg>
  -o, --output-dir  <arg>
  -f, --output-format  <arg>
      --help                   Show help message

Subcommand: generate-data kmeans
  -k, --k  <arg>
  -s, --partitions  <arg>
  -m, --scaling  <arg>
      --help                Show help message
Subcommand: workload
  -i, --input-dir  <arg>
      --input-format  <arg>
  -o, --output-dir  <arg>
  -f, --output-format  <arg>
      --workload-results-output-dir  <arg>
      --workload-results-output-format  <arg>
      --help                                    Show help message

Subcommand: workload kmeans
  -k, --k  <arg>
  -m, --max-iterations  <arg>
  -s, --seed  <arg>
      --help                    Show help message
 ```