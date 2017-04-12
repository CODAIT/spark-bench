#!/bin/bash

export SPARK_BENCH_JAR=./target/scala-2.11/spark-bench.jar
export SPARK_HOME=/opt/spark-2.1.0-bin-hadoop2.6
export SPARK_MASTER_HOST=local[2]

#rm -rf /tmp/coolstuff1
#rm -rf /tmp/coolstuff2
rm -rf /Users/ecurtin/Desktop/test-results.csv
rm -rf /Users/ecurtin/Desktop/test-results1.csv
rm -rf /Users/ecurtin/Desktop/test-results2.csv
rm -rf /Users/ecurtin/Desktop/test-results3.csv
rm -rf /Users/ecurtin/Desktop/test-results4.csv
rm -rf /Users/ecurtin/Desktop/test-results5.csv

# --------------
# Generate data
#./bin/spark-bench.sh generate-data -r 5 -c 5 -o /tmp/coolstuff1 --output-format csv kmeans
#
#./bin/spark-bench.sh generate-data -r 3000 -c 200 -o /tmp/coolstuff2 --output-format csv kmeans -k 20

# --------------

# One workload config, run 5 times
./bin/spark-bench.sh workload -i /tmp/coolstuff1 -o /Users/ecurtin/Desktop/test-results1.csv -n 5 kmeans -k 2

# Four workload configs, each run once
./bin/spark-bench.sh workload -i /tmp/coolstuff1 /tmp/coolstuff2 -o /Users/ecurtin/Desktop/test-results2.csv kmeans -k 2 32

# Four workload configs, each run 5 times
./bin/spark-bench.sh workload -i /tmp/coolstuff1 /tmp/coolstuff2 -o /Users/ecurtin/Desktop/test-results3.csv -n 5 kmeans -k 2 32

# Four workload configs, run at the same time in parallel on one SparkSession, that parallel "set" is only run once
./bin/spark-bench.sh workload -i /tmp/coolstuff1 /tmp/coolstuff2 -o /Users/ecurtin/Desktop/test-results4.csv --parallel kmeans -k 2 32

# Four workload configs, run at the same time in parallel on one SparkSession, that parallel "set" is run 5 times
./bin/spark-bench.sh workload -i /tmp/coolstuff1 /tmp/coolstuff2 -o /Users/ecurtin/Desktop/test-results5.csv --parallel -n 5 kmeans -k 2 32
