<!-- 
 (C) Copyright IBM Corp. 2015 - 2017

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# spark-bench
## Benchmark Suite for Apache Spark

[![GitHub issues](https://img.shields.io/github/release/SparkTC/spark-bench.svg)](https://github.com/SparkTC/spark-bench/releases/latest)
[![Build Status](https://travis-ci.org/SparkTC/spark-bench.svg?branch=master)](https://travis-ci.org/SparkTC/spark-bench)
[![codecov](https://codecov.io/gh/SparkTC/spark-bench/branch/master/graph/badge.svg)](https://codecov.io/gh/SparkTC/spark-bench)
<a href="https://github.com/SparkTC/spark-bench#boards?repos=40686427"><img src="https://raw.githubusercontent.com/ZenHubIO/support/master/zenhub-badge.png"></a>

# READ OUR DOCS
The documentation for Spark-Bench is all in our shiny new docs site: <https://sparktc.github.io/spark-bench/>

# Versions And Compatibility

## Spark Version

Spark-Bench is currently compiled against the Spark 2.1.1 jars and should work with Spark 2.x.
If you experience compatibility issues between Spark-Bench and any 2.x version of Spark, please let us know!

## Scala Version

Spark-Bench is written using Scala 2.11.8. It is _incompatible_ with Spark versions running Scala 2.10.x

# Installation

Follow the [Quickstart guide](https://sparktc.github.io/spark-bench/quickstart/) from our docs site. For more details, see the [Installation page](https://sparktc.github.io/spark-bench/installation).

# Legacy Version

spark-bench has recently gone through an extensive rewrite.
While we think you'll like the new capabilities, it is not quite feature complete with the previous version of spark-bench.
Many of the workloads that were available in the legacy have not yet been ported over, but they will be!

In the meantime, if you would like to see the old version of spark-bench, it's preserved in [the legacy branch](https://github.com/SparkTC/spark-bench/tree/legacy).

You can also grab the last official release of the legacy version [from here](https://github.com/SparkTC/spark-bench/releases/tag/SparkBench_spark-v1.6).
