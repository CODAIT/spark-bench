#!/bin/bash

export JAR=/Users/ecurtin/git/spark-bench/cli/target/scala-2.11/cli-assembly-2.0.0-SNAPSHOT.jar
export SPARK_HOME=/opt/spark-2.1.0-bin-hadoop2.6
export SPARK_MASTER_HOST=local[2]


./bin/spark-bench.sh generate-data -r 100 kmeans