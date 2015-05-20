#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo "========== running kmeans bench =========="
# configure
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"

if [ $COMPRESS -eq 1 ]; then
    COMPRESS_OPT="-Dmapred.output.compress=true
    -Dmapred.output.compression.codec=$COMPRESS_CODEC"
else
    COMPRESS_OPT="-Dmapred.output.compress=false"
fi

# path check
$HADOOP_HOME/bin/hadoop dfs -rm-r ${OUTPUT_HDFS}

# pre-running
SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $2 }'`

JAR="${DIR}/target/scala-2.10/kmeans-app_2.10-1.0.jar"
CLASS="KmeansApp"
OPTION=" ${INPUT_HDFS} ${OUTPUT_HDFS} ${NUM_OF_CLUSTERS} ${MAX_ITERATION} ${NUM_RUN}"

START_TIME=`timestamp`

# run bench

exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${SPARK_MASTER} $JAR ${OPTION} 2>&1|tee /tmp/kmeans_run.log

# post-running
END_TIME=`timestamp`

gen_report "KMEANS" ${START_TIME} ${END_TIME} ${SIZE} >> ${BENCH_REPORT}