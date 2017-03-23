#!/bin/bash

export SPARK_BENCH_JAR=./cli/target/scala-2.11/cli-assembly-2.1.0-SNAPSHOT.jar
export SPARK_HOME=/opt/spark-2.1.0-bin-hadoop2.6
export SPARK_MASTER_HOST=local[2]

#rm -rf /tmp/coolstuff

./bin/spark-bench.sh generate-data -r 5 -c 5 -o /tmp/coolstuff --output-format csv kmeans
./bin/spark-bench.sh workload -i /tmp/coolstuff -o ~/Desktop/test-results/ kmeans
