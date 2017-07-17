#!/bin/bash

WHEREILIVE=$(realpath $0)
BASEDIR=$(dirname "$WHEREILIVE")
PARENTDIR=$(dirname "$BASEDIR")

[[ -f "$BASEDIR/spark-bench-env.sh" ]] && source "$BASEDIR/spark-bench-env.sh"
[[ -z "$SPARK_HOME" ]] && echo "[ERROR] Please set the environment variable SPARK_HOME in bin/spark-bench-env.sh" && exit 1
[[ -f "$SPARK_HOME/conf/spark-env.sh" ]] && source "$SPARK_HOME/conf/spark-env.sh"

SPARK_BENCH_LAUNCH_JAR=$(ls "$PARENTDIR"/lib/spark-bench-launch*.jar)
MAIN_CLASS="com.ibm.sparktc.sparkbench.sparklaunch.SparkLaunch"
SPARK_BENCH_JAR=$(ls "$PARENTDIR"/lib/spark-bench-[0-9]*.jar)
#SPARK_BENCH_CLASSPATH="$SPARK_BENCH_JAR"

#for dir in "$SPARK_HOME/lib" "$SPARK_HOME/jars" "$WORKLOAD_DIR"
#    do [[ -d "$dir" ]] && echo "Found $dir" && SPARK_BENCH_CLASSPATH+=":$dir/*"
#done

#echo "SPARK_BENCH_CLASSPATH is $SPARK_BENCH_CLASSPATH"
echo "MAIN_CLASS is $MAIN_CLASS"
#export SPARK_BENCH_CLASSPATH

java -cp "$SPARK_BENCH_LAUNCH_JAR" "$MAIN_CLASS" "$@"
