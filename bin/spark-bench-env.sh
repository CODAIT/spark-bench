#!/bin/bash

export SPARK_BENCH_JAR=../spark-bench_*.jar
export SPARK_HOME=/opt/spark-2.1.0-bin-hadoop2.6
export SPARK_MASTER_HOST=local[2]

#TODO: change this to a template. Would make sense to wrap this change into building multiple spark-submits