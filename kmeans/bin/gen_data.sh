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
JAR="${DIR}/target/scala-2.10/kmeans-app_2.10-1.0.jar"
CLASS="kmeans_min.src.main.scala.KmeansDataGen"

OPTION="${NUM_OF_POINTS} ${NUM_OF_CLUSTERS} ${DIMENSIONS} ${SCALING} ${NUM_OF_PARTITION} ${INPUT_HDFS}"


exec ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${SPARK_MASTER} $JAR ${OPTION} 2>&1|tee /tmp/kmeans_gendata.log
exit 0
