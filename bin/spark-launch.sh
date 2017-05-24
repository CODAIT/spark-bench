#!/usr/bin/env bash


WHEREILIVE=$(realpath $0)
BASEDIR=$(dirname ${WHEREILIVE})
PARENTDIR="$(dirname "${BASEDIR}")"

source ${BASEDIR}/spark-bench-env.sh

[ -z "$SPARK_HOME" ] && echo "Please set the environment variable SPARK_HOME in bin/spark-bench-env.sh" && exit 1;
[ -z "$SPARK_MASTER_HOST" ] && echo "Please set the environment variable SPARK_MASTER_HOST in bin/spark-bench-env.sh" && exit 1;


SB_JAR=`ls ${PARENTDIR}/lib/`

export SPARK_BENCH_JAR=${PARENTDIR}/lib/${SB_JAR}
MAIN_CLASS=com.ibm.sparktc.sparkbench.sparklaunch.SparkLaunch

java -cp $SPARK_BENCH_JAR $MAIN_CLASS "$@"