#!/bin/bash

WHEREILIVE=$(realpath $0)
BASEDIR=$(dirname ${WHEREILIVE})

source ${BASEDIR}/spark-bench-env.sh

[ -z "$SPARK_HOME" ] && echo "Please set the environment variable SPARK_HOME in bin/spark-bench-env.sh" && exit 1;
[ -z "$SPARK_MASTER_HOST" ] && echo "Please set the environment variable SPARK_MASTER_HOST in bin/spark-bench-env.sh" && exit 1;


${SPARK_HOME}/bin/spark-submit \
        --class com.ibm.sparktc.sparkbench.cli.CLIKickoff \
        --master ${SPARK_MASTER_HOST} \
        ${SPARK_BENCH_JAR} \
        "$@"
