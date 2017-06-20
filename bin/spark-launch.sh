#!/bin/bash

WHEREILIVE=$(realpath $0)
BASEDIR=$(dirname ${WHEREILIVE})
PARENTDIR="$(dirname "${BASEDIR}")"

source ${BASEDIR}/spark-bench-env.sh

[ -z "$SPARK_HOME" ] && echo "Please set the environment variable SPARK_HOME in bin/spark-bench-env.sh" && exit 1;
[ -z "$SPARK_MASTER_HOST" ] && echo "Please set the environment variable SPARK_MASTER_HOST in bin/spark-bench-env.sh" && exit 1;

if [ -f $SPARK_HOME/conf/spark-env.sh ]; then
    source $SPARK_HOME/conf/spark-env.sh
fi

SB_JAR=`ls ${PARENTDIR}/lib/ | grep spark-bench-launch`

SPARK_BENCH_JAR="${PARENTDIR}/lib/${SB_JAR}"
MAIN_CLASS="com.ibm.sparktc.sparkbench.sparklaunch.SparkLaunch"

SPARK_BENCH_CLASSPATH="${SPARK_BENCH_JAR}"

if [ -d ${SPARK_HOME}/lib ]; then
    echo Found lib
    SPARK_BENCH_CLASSPATH+=":${SPARK_HOME}/lib/*"
fi

if [ -d ${SPARK_HOME}/jars ]; then
    echo Found jars
    SPARK_BENCH_CLASSPATH+=":${SPARK_HOME}/jars/*"
fi

echo SPARK_BENCH_CLASSPATH is $SPARK_BENCH_CLASSPATH
echo MAIN_CLASS is $MAIN_CLASS

java -cp "$SPARK_BENCH_CLASSPATH" "$MAIN_CLASS" "$@"
