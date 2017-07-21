#!/bin/bash

WHEREILIVE=$(realpath $0)
BASEDIR=$(dirname "$WHEREILIVE")
PARENTDIR=$(dirname "$BASEDIR")

[[ -f "$BASEDIR/spark-bench-env.sh" ]] && source "$BASEDIR/spark-bench-env.sh"

SPARK_BENCH_LAUNCH_JAR=$(ls "$PARENTDIR"/lib/spark-bench-launch*.jar)
MAIN_CLASS="com.ibm.sparktc.sparkbench.sparklaunch.SparkLaunch"
SPARK_BENCH_JAR=$(ls "$PARENTDIR"/lib/spark-bench-[0-9]*.jar)

java -cp "$SPARK_BENCH_LAUNCH_JAR" "$MAIN_CLASS" "$@"
