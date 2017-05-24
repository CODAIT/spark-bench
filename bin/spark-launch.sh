#!/usr/bin/env bash

WHEREILIVE=$(realpath $0)
BASEDIR=$(dirname $WHEREILIVE)
PARENTDIR="$(dirname "${BASEDIR}")"

SB_JAR=`ls ${PARENTDIR}/lib/`

export SPARK_BENCH_JAR=${PARENTDIR}/lib/${SB_JAR}
MAIN_CLASS=com.ibm.sparktc.sparkbench.sparklaunch.SparkLaunch

java -cp $SPARK_BENCH_JAR $MAIN_CLASS "$@"