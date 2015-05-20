#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo "========== preparing kmeans data =========="
# configure
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"


# compress check
if [ ${COMPRESS_GLOBAL} -eq 1 ]; then
    COMPRESS_OPT="-compress true \
        -compressCodec $COMPRESS_CODEC \
        -compressType BLOCK "
else
    COMPRESS_OPT="-compress false"
fi

# paths check
${HADOOP_HOME}/bin/hadoop fs -rm -r ${INPUT_HDFS}

# generate data
JAR="${DIR}/target/kmeans-project-1.0.jar"
CLASS="kmeans_java.src.main.java.KmeansGenData"

OPTION="${NUM_OF_POINTS} ${NUM_OF_CLUSTERS} ${DIMENSIONS} ${SCALING} ${NUM_OF_PARTITIONS} ${INPUT_HDFS}"

START_TIME=`timestamp`

exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/${APP}_gendata_${START_TS}.dat

END_TIME=`timestamp`

SIZE=`$HADOOP_HOME/bin/hadoop fs -du -s ${INPUT_HDFS} | awk '{ print $1 }'`

gen_report "KMEANS-gendata" ${START_TIME} ${END_TIME} ${SIZE} >> ${BENCH_REPORT}
exit 0
