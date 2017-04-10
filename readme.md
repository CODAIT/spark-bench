# spark-bench
## Benchmark Suite for Apache Spark

[![Build Status](https://travis-ci.org/ecurtin/spark-bench.svg?branch=master)](https://travis-ci.org/ecurtin/spark-bench)
[![codecov](https://codecov.io/gh/ecurtin/spark-bench/branch/master/graph/badge.svg)](https://codecov.io/gh/ecurtin/spark-bench)


### Current Spark and spark-bench version: 2.1.0

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [History](#history)
- [Installation](#installation)
- [Usage](#usage)
- [Multiple Arguments](#multiple-arguments)
- [Parallel Runs](#parallel-runs)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

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
 
 ## Multiple Arguments
 You can have single arguments or a space-separated list of arguments that will be taken in as a Sequence.
 This is best understood through example:
 
 `./bin/spark-bench.sh workload -i /tmp/input-data1 -o /Users/ecurtin/Desktop/test-results.csv kmeans -k 2`
 will run kmeans 1 time over the /tmp/input-data1 set and a k value of 2.
 
 `./bin/spark-bench.sh workload -i /tmp/input-data1 /tmp/input-data2 -o /Users/ecurtin/Desktop/test-results.csv kmeans -k 2`
 will run kmeans 2 times, once over the /tmp/input-data1 set with a k value of 2, and another time over the /tmp/input-data2 set with a k-value of 2.
 
 `./bin/spark-bench.sh workload -i /tmp/input-data1 /tmp/input-data2 -o /Users/ecurtin/Desktop/test-results.csv kmeans -k 2 32`
 will run kmeans 4 times with all combinations of /tmp/input-data1, /tmp/input-data2, k = 2, k = 32.
 
 ## Parallel Runs
 Workloads can be run serially or in parallel across a SparkSession. By default, workloads will be run one after the other. 
 Specify `--parallel` to make your workloads run in parallel on the SparkSession.
 
 `./bin/spark-bench.sh workload -i /tmp/input-data1 /tmp/input-data2 -o /Users/ecurtin/Desktop/test-results.csv --parallel kmeans -k 2 32`
 will run kmeans 4 times over the two datasets and k values, but unlike the example above it will launch all four variations of the workload in parallel in one SparkSession.
