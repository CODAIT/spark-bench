#!/bin/bash

rm -rf /tmp/spark-bench-demo/kmeans-dataset-1
rm -rf /tmp/spark-bench-demo/kmeans-dataset-2
rm -rf /tmp/spark-bench-demo/test-results.csv
rm -rf /tmp/spark-bench-demo/test-results1.csv
rm -rf /tmp/spark-bench-demo/test-results2.csv
rm -rf /tmp/spark-bench-demo/test-results3.csv
rm -rf /tmp/spark-bench-demo/test-results4.csv
rm -rf /tmp/spark-bench-demo/test-results5.csv

# --------------
# Generate data
# --------------

bin/spark-bench.sh generate-data kmeans -r 5 -c 5 -o /tmp/spark-bench-demo/kmeans-dataset-1.csv --output-format csv
#
bin/spark-bench.sh generate-data kmeans -r 3000 -c 200 -o /tmp/spark-bench-demo/kmeans-dataset-2.parquet -k 20



# --------------
# Run workloads
# --------------

# One workload config, run 5 times
bin/spark-bench.sh workload kmeans -i /tmp/spark-bench-demo/kmeans-dataset-1.csv -o /tmp/spark-bench-demo/test-results1.csv -n 5 -k 2

# Four workload configs, each run once
bin/spark-bench.sh workload kmeans -i /tmp/spark-bench-demo/kmeans-dataset-1.csv /tmp/spark-bench-demo/kmeans-dataset-2.parquet -o /tmp/spark-bench-demo/test-results2.csv -k 2 32

# Four workload configs, each run 5 times
bin/spark-bench.sh workload kmeans -i /tmp/spark-bench-demo/kmeans-dataset-1.csv /tmp/spark-bench-demo/kmeans-dataset-2.parquet -o /tmp/spark-bench-demo/test-results3.csv -n 5 -k 2 32

# Four workload configs, run at the same time in parallel on one SparkSession, that parallel "set" is only run once
bin/spark-bench.sh workload kmeans -i /tmp/spark-bench-demo/kmeans-dataset-1.csv /tmp/spark-bench-demo/kmeans-dataset-2.parquet -o /tmp/spark-bench-demo/test-results4.csv --parallel -k 2 32

# Four workload configs, run at the same time in parallel on one SparkSession, that parallel "set" is run 5 times
bin/spark-bench.sh workload kmeans -i /tmp/spark-bench-demo/kmeans-dataset-1.csv /tmp/spark-bench-demo/kmeans-dataset-2.parquet -o /tmp/spark-bench-demo/test-results5.csv --parallel -n 5 -k 2 32



# ------------------------------
# Run from a configuration file
# ------------------------------

# Runs spark-bench from example-configuration-file.conf
bin/spark-bench.sh ./example-configuration-file.conf
