#!/bin/bash

export SPARK_BENCH_JAR=./cli/target/scala-2.11/cli-assembly-2.1.0-SNAPSHOT.jar
export SPARK_HOME=/opt/spark-2.1.0-bin-hadoop2.6
export SPARK_MASTER_HOST=local[2]

#rm -rf /tmp/coolstuff

#./bin/spark-bench.sh generate-data -r 5 -c 5 -o /tmp/coolstuff1 --output-format csv kmeans
#
#./bin/spark-bench.sh generate-data -r 3000 -c 200 -o /tmp/coolstuff2 --output-format csv kmeans -k 20

rm -rf /Users/ecurtin/Desktop/test-results.csv

./bin/spark-bench.sh workload -i /tmp/coolstuff1 /tmp/coolstuff2 -o /Users/ecurtin/Desktop/test-results.csv kmeans -k 2 32
